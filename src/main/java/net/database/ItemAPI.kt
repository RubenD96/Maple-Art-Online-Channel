package net.database

import client.Character
import client.Client
import client.interaction.storage.ItemStorage
import client.inventory.ItemInventory
import client.inventory.ItemInventoryType
import client.inventory.item.slots.*
import client.inventory.item.templates.ItemBundleTemplate
import client.inventory.item.templates.ItemEquipTemplate
import database.jooq.Tables.*
import managers.ItemManager
import net.database.DatabaseCore.connection
import org.jooq.Record
import org.jooq.exception.DataAccessException
import util.HexTool
import util.logging.LogType
import util.logging.Logger.log
import java.util.*
import kotlin.collections.HashSet

object ItemAPI {

    fun loadItemInventories(chr: Character) {
        with(ITEMINVENTORIES) {
            val itemData = connection.select().from(this)
                .leftOuterJoin(ITEMSLOTS).on(ITEMSLOTS.ID.eq(SLOTID))
                .leftOuterJoin(ITEMSLOTBUNDLES).on(ITEMSLOTBUNDLES.SLOTID.eq(SLOTID))
                .leftOuterJoin(ITEMSLOTEQUIPS).on(ITEMSLOTEQUIPS.SLOTID.eq(SLOTID))
                .leftOuterJoin(ITEMSLOTPETS).on(ITEMSLOTPETS.SLOTID.eq(SLOTID))
                .where(CID.eq(chr.id))
                .fetch()

            itemData.forEach {
                val item = loadItemSlot(it)

                val items = chr.getInventory(item.templateId / 1000000 - 1).items
                items[it.getValue(ITEMSLOTS.POSITION)] = item
            }
        }

        with(ITEMSTORAGES) {
            val itemData = connection.select().from(this)
                .leftOuterJoin(ITEMSLOTS).on(ITEMSLOTS.ID.eq(SLOTID))
                .leftOuterJoin(ITEMSLOTBUNDLES).on(ITEMSLOTBUNDLES.SLOTID.eq(SLOTID))
                .leftOuterJoin(ITEMSLOTEQUIPS).on(ITEMSLOTEQUIPS.SLOTID.eq(SLOTID))
                .leftOuterJoin(ITEMSLOTPETS).on(ITEMSLOTPETS.SLOTID.eq(SLOTID))
                .where(AID.eq(chr.client.accId))
                .fetch()

            itemData.forEach {
                val item = loadItemSlot(it)

                chr.client.storage.items[it.getValue(ITEMSLOTS.POSITION)] = item
            }
        }

        with(ITEMLOCKERS) {
            val itemData = connection.select().from(this)
                .leftOuterJoin(ITEMSLOTS).on(ITEMSLOTS.ID.eq(SLOTID))
                .leftOuterJoin(ITEMSLOTBUNDLES).on(ITEMSLOTBUNDLES.SLOTID.eq(SLOTID))
                .leftOuterJoin(ITEMSLOTEQUIPS).on(ITEMSLOTEQUIPS.SLOTID.eq(SLOTID))
                .leftOuterJoin(ITEMSLOTPETS).on(ITEMSLOTPETS.SLOTID.eq(SLOTID))
                .where(AID.eq(chr.client.accId))
                .fetch()

            itemData.forEach {
                val item = loadItemSlot(it)

                chr.client.locker.add(ItemSlotLocker(item))
            }
        }
    }

    private fun loadItemSlot(rec: Record): ItemSlot {
        with(ITEMSLOTS) {
            val template = ItemManager.getItem(rec.getValue(TEMPLATEID))
            val item = template.toItemSlot()

            item.uuid = rec.getValue(ID)
            item.cashItemSN = rec.getValue(CASHITEMSN)
            item.expire = rec.getValue(EXPIRE)
            item.isNewItem = false

            when (item) {
                is ItemSlotBundle -> loadItemSlotBundle(rec, item)
                is ItemSlotEquip -> loadItemSlotEquip(rec, item)
                is ItemSlotPet -> loadItemSlotPet(rec, item)
            }

            item.updated = false

            return item
        }
    }

    private fun loadItemSlotBundle(rec: Record, item: ItemSlotBundle) {
        with(ITEMSLOTBUNDLES) {
            item.number = rec.getValue(NUMBER)
            item.attribute = rec.getValue(ATTRIBUTE)
            item.title = rec.getValue(TITLE)
        }
    }

    private fun loadItemSlotEquip(rec: Record, item: ItemSlotEquip) {
        with(ITEMSLOTEQUIPS) {
            item.ruc = rec.getValue(RUC)
            item.cuc = rec.getValue(CUC)
            item.str = rec.getValue(STR)
            item.dex = rec.getValue(DEX)
            item.int = rec.getValue(INT)
            item.luk = rec.getValue(LUK)
            item.maxHP = rec.getValue(MAXHP)
            item.maxMP = rec.getValue(MAXMP)
            item.pad = rec.getValue(PAD)
            item.mad = rec.getValue(MAD)
            item.pdd = rec.getValue(PDD)
            item.mdd = rec.getValue(MDD)
            item.acc = rec.getValue(ACC)
            item.eva = rec.getValue(EVA)
            item.craft = rec.getValue(CRAFT)
            item.speed = rec.getValue(SPEED)
            item.jump = rec.getValue(JUMP)
            item.attribute = rec.getValue(ATTRIBUTE)
            item.title = rec.getValue(TITLE)
            item.level = rec.getValue(LEVEL)
            item.exp = rec.getValue(EXP)
            item.durability = rec.getValue(DURABILITY)
            item.iuc = rec.getValue(IUC)
            item.grade = rec.getValue(GRADE)
            item.chuc = rec.getValue(CHUC)
            item.option1 = rec.getValue(OPTION1)
            item.option2 = rec.getValue(OPTION2)
            item.option3 = rec.getValue(OPTION3)
        }
    }

    private fun loadItemSlotPet(rec: Record, item: ItemSlotPet) {
        with(ITEMSLOTPETS) {
            item.petName = rec.getValue(PETNAME)
            item.level = rec.getValue(LEVEL)
            item.repleteness = rec.getValue(REPLETENESS)
            item.tameness = rec.getValue(TAMENESS)
            item.petAttribute = rec.getValue(PETATTRIBUTE)
            item.petSkill = rec.getValue(PETSKILL)
            item.attribute = rec.getValue(ATTRIBUTE)
            item.dateDead = rec.getValue(DATEDEAD)
            item.remainLife = rec.getValue(REMAINLIFE)
        }
    }

    fun saveItemInventories(chr: Character) {
        var start = System.currentTimeMillis()
        val toUpdate = deleteOldItems(chr)
        println("Old removed in ${(System.currentTimeMillis() - start)}ms")

        // inventories
        start = System.currentTimeMillis()
        chr.allInventories.forEach { (type, inv) ->
            inv.items.forEach { (slot: Short, item: ItemSlot) ->
                if (toUpdate.contains(item)) { // item.updated = true
                    updateItemSlot(item, slot)
                } else if (item.isNewItem) { // item.newItem = true
                    insertNewItemSlot(chr, item, slot, DBInventoryType.INVENTORY)
                } // old items are ignored, no need to update or insert, if they're removed its already done
            }
        }
        println("Inventories updated in ${(System.currentTimeMillis() - start)}ms")

        // storage
        start = System.currentTimeMillis()
        updateAccountStorageStats(chr.client)
        chr.client.storage.items.forEach { (slot: Short, item: ItemSlot) ->
            if (toUpdate.contains(item)) {
                updateItemSlot(item, slot)
            } else if (item.isNewItem) {
                insertNewItemSlot(chr, item, slot, DBInventoryType.STORAGE)
            }
        }
        println("Storage updated in ${(System.currentTimeMillis() - start)}ms")

        // locker
        start = System.currentTimeMillis()
        chr.client.locker.forEach { item: ItemSlotLocker ->
            if (toUpdate.contains(item.item)) {
                updateItemSlot(item.item, item.position)
            } else if (item.item.isNewItem) {
                insertNewItemSlot(chr, item.item, item.position, DBInventoryType.LOCKER)
            }
        }
        println("Storage updated in ${(System.currentTimeMillis() - start)}ms")
    }

    private fun updateAccountStorageStats(c: Client) {
        with(ACCOUNTS) {
            connection.update(this)
                .set(STORAGESLOTS, c.storage.slotMax)
                .set(STOREDMESO, c.storage.meso)
                .execute()
        }
    }

    private fun updateItemSlot(item: ItemSlot, position: Short) {
        println("Update me!! $item")

        with(ITEMSLOTS) {
            connection.update(this)
                .set(POSITION, position)
                .set(CASHITEMSN, item.cashItemSN)
                .set(EXPIRE, item.expire)
                .where(ID.eq(item.uuid))
                .execute()
        }

        when (item) {
            is ItemSlotBundle -> updateItemSlotBundle(item)
            is ItemSlotEquip -> updateItemSlotEquip(item)
            is ItemSlotPet -> updateItemSlotPet(item)
        }
    }

    private fun updateItemSlotBundle(item: ItemSlotBundle) {
        with(ITEMSLOTBUNDLES) {
            connection.update(this)
                .set(NUMBER, item.number)
                .set(ATTRIBUTE, item.attribute)
                .set(TITLE, item.title)
                .where(SLOTID.eq(item.uuid))
                .execute()
        }
    }

    private fun updateItemSlotEquip(item: ItemSlotEquip) {
        with(ITEMSLOTEQUIPS) {
            connection.update(this)
                .set(RUC, item.ruc)
                .set(CUC, item.cuc)
                .set(STR, item.str)
                .set(DEX, item.dex)
                .set(INT, item.int)
                .set(LUK, item.luk)
                .set(MAXHP, item.maxHP)
                .set(MAXMP, item.maxMP)
                .set(PAD, item.pad)
                .set(MAD, item.mad)
                .set(PDD, item.pdd)
                .set(MDD, item.mdd)
                .set(ACC, item.acc)
                .set(EVA, item.eva)
                .set(CRAFT, item.craft)
                .set(SPEED, item.speed)
                .set(JUMP, item.jump)
                .set(ATTRIBUTE, item.attribute)
                .set(TITLE, item.title)
                .set(LEVEL, item.level)
                .set(EXP, item.exp)
                .set(DURABILITY, item.durability)
                .set(IUC, item.iuc)
                .set(GRADE, item.grade)
                .set(CHUC, item.chuc)
                .set(OPTION1, item.option1)
                .set(OPTION2, item.option2)
                .set(OPTION3, item.option3)
                .where(SLOTID.eq(item.uuid))
                .execute()
        }
    }

    private fun updateItemSlotPet(item: ItemSlotPet) {
        with(ITEMSLOTPETS) {
            connection.update(this)
                .set(PETNAME, item.petName)
                .set(LEVEL, item.level)
                .set(REPLETENESS, item.repleteness)
                .set(TAMENESS, item.tameness)
                .set(PETATTRIBUTE, item.petAttribute)
                .set(PETSKILL, item.petSkill)
                .set(ATTRIBUTE, item.attribute)
                .set(DATEDEAD, item.dateDead)
                .set(REMAINLIFE, item.remainLife)
                .where(SLOTID.eq(item.uuid))
                .execute()
        }
    }

    private enum class DBInventoryType {
        INVENTORY, STORAGE, LOCKER
    }

    private fun insertNewItemSlot(
        chr: Character,
        item: ItemSlot,
        position: Short,
        type: DBInventoryType
    ) {
        insertNewItemSlot(chr.id, chr.client.accId, item, position, type)
    }

    private fun insertNewItemSlot(
        id: Int,
        aid: Int,
        item: ItemSlot,
        position: Short,
        type: DBInventoryType
    ) {
        println("Insert me!! $item")
        item.uuid ?: run {
            val uuid = HexTool.toBytes(UUID.randomUUID().toString().replace("-", ""))
            item.uuid = uuid
        }

        with(ITEMSLOTS) {
            //val type: Byte = (item.templateId / 1000000 - 1).toByte()
            connection.insertInto(this, ID, POSITION, TEMPLATEID, CASHITEMSN, EXPIRE)
                .values(item.uuid, position, item.templateId, item.cashItemSN, item.expire)
                .execute()
        }

        when (type) {
            DBInventoryType.INVENTORY -> {
                with(ITEMINVENTORIES) {
                    connection.insertInto(this, SLOTID, CID).values(item.uuid, id).execute()
                }
            }
            DBInventoryType.STORAGE -> {
                with(ITEMSTORAGES) {
                    connection.insertInto(this, SLOTID, AID).values(item.uuid, aid).execute()
                }
            }
            DBInventoryType.LOCKER -> {
                with(ITEMLOCKERS) {
                    val giftFrom = item // todo
                    connection.insertInto(this, SLOTID, AID, BUYCHARACTERNAME).values(item.uuid, aid, "")
                        .execute()
                }
            }
        }

        when (item) {
            is ItemSlotBundle -> insertNewItemSlotBundle(item)
            is ItemSlotEquip -> insertNewItemSlotEquip(item)
            is ItemSlotPet -> insertNewItemSlotPet(item)
        }
    }

    private fun insertNewItemSlotBundle(item: ItemSlotBundle) {
        with(ITEMSLOTBUNDLES) {
            connection.insertInto(this, SLOTID, NUMBER, ATTRIBUTE, TITLE)
                .values(item.uuid, item.number, item.attribute, item.title)
                .execute()
        }
    }

    private fun insertNewItemSlotEquip(item: ItemSlotEquip) {
        with(ITEMSLOTEQUIPS) {
            connection.insertInto(
                this,
                SLOTID,
                RUC,
                CUC,
                STR,
                DEX,
                INT,
                LUK,
                MAXHP,
                MAXMP,
                PAD,
                MAD,
                PDD,
                MDD,
                ACC,
                EVA,
                CRAFT,
                SPEED,
                JUMP,
                ATTRIBUTE,
                TITLE,
                LEVEL,
                EXP,
                DURABILITY,
                IUC,
                GRADE,
                CHUC,
                OPTION1,
                OPTION2,
                OPTION3
            )
                .values(
                    item.uuid,
                    item.ruc,
                    item.cuc,
                    item.str,
                    item.dex,
                    item.int,
                    item.luk,
                    item.maxHP,
                    item.maxMP,
                    item.pad,
                    item.mad,
                    item.pdd,
                    item.mdd,
                    item.acc,
                    item.eva,
                    item.craft,
                    item.speed,
                    item.jump,
                    item.attribute,
                    item.title,
                    item.level,
                    item.exp,
                    item.durability,
                    item.iuc,
                    item.grade,
                    item.chuc,
                    item.option1,
                    item.option2,
                    item.option3
                )
                .execute()
        }
    }

    private fun insertNewItemSlotPet(item: ItemSlotPet) {
        with(ITEMSLOTPETS) {
            connection.insertInto(
                this,
                SLOTID,
                PETNAME,
                LEVEL,
                REPLETENESS,
                TAMENESS,
                PETATTRIBUTE,
                PETSKILL,
                ATTRIBUTE,
                DATEDEAD,
                REMAINLIFE
            )
                .values(
                    item.uuid,
                    item.petName,
                    item.level,
                    item.repleteness,
                    item.tameness,
                    item.petAttribute,
                    item.petSkill,
                    item.attribute,
                    item.dateDead,
                    item.remainLife
                )
                .execute()
        }
    }

    private fun deleteOldItems(chr: Character): Set<ItemSlot> {
        val toUpdate: MutableSet<ItemSlot> = HashSet()

        val toDeleteInv: MutableSet<ByteArray> = HashSet()
        with(ITEMINVENTORIES) {
            val inventories = connection.select(SLOTID)
                .from(this)
                .where(CID.eq(chr.id))
                .fetch()

            inventories.forEach { rec ->
                val uuid = rec.getValue(SLOTID)
                toDeleteInv.add(uuid)

                ItemInventoryType.values().forEach { type ->
                    val inv = chr.getInventory(type)
                    val item = inv.items.values.firstOrNull { item -> Arrays.equals(item.uuid, uuid) }
                    if (item != null) {
                        toDeleteInv.remove(uuid)

                        if (item.updated) {
                            toUpdate.add(item)
                        }
                    }
                }
            }
        }

        val toDeleteStorage: MutableSet<ByteArray> = HashSet()
        with(ITEMSTORAGES) {
            val storage = connection.select(SLOTID)
                .from(this)
                .where(AID.eq(chr.client.accId))
                .fetch()

            storage.forEach { rec ->
                val uuid = rec.getValue(SLOTID)
                toDeleteStorage.add(uuid)

                val storageItem = chr.client.storage.items.values.firstOrNull { item -> Arrays.equals(item.uuid, uuid) }
                if (storageItem != null) {
                    toDeleteStorage.remove(uuid)

                    if (storageItem.updated) {
                        toUpdate.add(storageItem)
                    }
                }
            }
        }

        val toDeleteLocker: MutableSet<ByteArray> = HashSet()
        with(ITEMLOCKERS) {
            val locker = connection.select(SLOTID)
                .from(this)
                .where(AID.eq(chr.client.accId))
                .fetch()

            locker.forEach { rec ->
                val uuid = rec.getValue(SLOTID)
                toDeleteLocker.add(uuid)

                val lockerItem = chr.client.locker.firstOrNull { item -> Arrays.equals(item.item.uuid, uuid) }
                if (lockerItem != null) {
                    toDeleteLocker.remove(uuid)

                    if (lockerItem.item.updated) {
                        toUpdate.add(lockerItem.item)
                    }
                }
            }
        }

        toDeleteInv.forEach {
            when {
                storageContains(chr.client.storage, it) ->
                    connection.insertInto(ITEMSTORAGES, ITEMSTORAGES.SLOTID, ITEMSTORAGES.AID)
                        .values(it, chr.client.accId)
                        .execute()
                lockerContains(chr.client.locker, it) ->
                    connection.insertInto(ITEMLOCKERS, ITEMLOCKERS.SLOTID, ITEMLOCKERS.AID)
                        .values(it, chr.client.accId)
                        .execute()
                else -> {
                    deleteItemByUUID(it)
                    return@forEach
                }
            }

            connection.deleteFrom(ITEMINVENTORIES)
                .where(ITEMINVENTORIES.SLOTID.eq(it))
                .execute()
        }

        toDeleteStorage.forEach {
            when {
                invContains(chr.allInventories.values, it) ->
                    connection.insertInto(ITEMINVENTORIES, ITEMINVENTORIES.SLOTID, ITEMINVENTORIES.CID)
                        .values(it, chr.id)
                        .execute()
                lockerContains(chr.client.locker, it) ->
                    connection.insertInto(ITEMLOCKERS, ITEMLOCKERS.SLOTID, ITEMLOCKERS.AID)
                        .values(it, chr.client.accId)
                        .execute()
                else -> {
                    deleteItemByUUID(it)
                    return@forEach
                }
            }

            connection.deleteFrom(ITEMSTORAGES)
                .where(ITEMSTORAGES.SLOTID.eq(it))
                .execute()
        }

        toDeleteLocker.forEach {
            when {
                invContains(chr.allInventories.values, it) ->
                    connection.insertInto(ITEMINVENTORIES, ITEMINVENTORIES.SLOTID, ITEMINVENTORIES.CID)
                        .values(it, chr.id)
                        .execute()
                storageContains(chr.client.storage, it) ->
                    connection.insertInto(ITEMSTORAGES, ITEMSTORAGES.SLOTID, ITEMSTORAGES.AID)
                        .values(it, chr.client.accId)
                        .execute()
                else -> {
                    deleteItemByUUID(it)
                    return@forEach
                }
            }

            connection.deleteFrom(ITEMLOCKERS)
                .where(ITEMLOCKERS.SLOTID.eq(it))
                .execute()
        }

        return toUpdate
    }

    fun deleteItemByUUID(uuid: ByteArray) {
        connection.deleteFrom(ITEMSLOTS)
            .where(ITEMSLOTS.ID.eq(uuid))
            .execute()
    }

    private fun invContains(inventories: Collection<ItemInventory>, uuid: ByteArray): Boolean {
        inventories.forEach { inv ->
            val item = inv.items.values.firstOrNull { it.uuid.contentEquals(uuid) }
            if (item != null) return true
        }
        return false
    }

    private fun storageContains(storage: ItemStorage, uuid: ByteArray): Boolean {
        return storage.items.values.firstOrNull { it.uuid.contentEquals(uuid) } != null
    }

    private fun lockerContains(locker: List<ItemSlotLocker>, uuid: ByteArray): Boolean {
        return locker.firstOrNull { it.item.uuid.contentEquals(uuid) } != null
    }

    /**
     * Saves the player's inventory.
     * Only insert if an new item has entered the player's inventory.
     * Only update if an item already existed and has been changed.
     *
     * @param chr The player to save
     */
    /*@Deprecated("old system")
    fun saveInventoriesOld(chr: Character) {
        var start = System.currentTimeMillis()
        val uuids = deleteOldItemsOld(chr)
        println("Old removed in ${(System.currentTimeMillis() - start)}ms")

        // inventories
        start = System.currentTimeMillis()
        chr.allInventories.forEach { (type, inv) ->
            inv.items.forEach { (slot: Short, item: ItemSlot) ->
                updateItemOld(chr, item, uuids, slot, type, 1)
            }
        }
        println("Inventories updated in ${(System.currentTimeMillis() - start)}ms")

        // storage
        start = System.currentTimeMillis()
        updateStorageStats(chr.client)
        chr.client.storage.items.forEach { (slot: Short, item: ItemSlot) ->
            updateItemOld(chr, item, uuids, slot, ItemInventoryType.values()[item.templateId / 1000000 - 1], 2)
        }
        println("Storage save in ${(System.currentTimeMillis() - start)}ms")
    }*/

    /*@Deprecated("old system")
    private fun updateItemOld(
        chr: Character,
        item: ItemSlot,
        uuids: Set<ByteArray>,
        slot: Short,
        invType: ItemInventoryType,
        storageType: Int
    ) {
        if (item.uuid == null || item.isNewItem && uuids.stream()
                .noneMatch { uuid -> Arrays.equals(item.uuid, uuid) }
        ) { // item is new, insert new entry
            insertNewItemOld(chr, storageType, invType.type, slot, item, null)
            if (invType == ItemInventoryType.EQUIP) {
                insertNewEquipOld(item as ItemSlotEquip)
            }
        } else if (item.updated) {
            println("Updating item ${item.templateId} on slot $slot for $chr")
            updateExistingItem(slot, item, storageType)
            if (invType == ItemInventoryType.EQUIP) {
                updateExistingEquip(item as ItemSlotEquip)
            }
        }
    }*/

    /**
     * Deletes old items that are no longer in the player's inventory
     *
     * @param chr The player to remove items from
     */
    /*@Deprecated("old system")
    private fun deleteOldItemsOld(chr: Character): Set<ByteArray> {
        val toDelete: MutableSet<ByteArray> = HashSet()
        val keep: MutableSet<ByteArray> = HashSet()
        val res = connection.select(INVENTORIES.ID)
            .from(INVENTORIES)
            .where(INVENTORIES.CID.eq(chr.id))
            .fetch()

        res.forEach { rec ->
            val uuid = rec.getValue(INVENTORIES.ID)
            toDelete.add(uuid)

            ItemInventoryType.values().forEach { type ->
                val inv = chr.getInventory(type)
                if (inv.items.values.stream().anyMatch { item: ItemSlot -> Arrays.equals(item.uuid, uuid) }) {
                    keep.add(uuid)
                    toDelete.remove(uuid)
                }
            }

            if (chr.client.locker.stream().anyMatch { item: ItemSlotLocker -> Arrays.equals(item.item.uuid, uuid) }) {
                keep.add(uuid)
                toDelete.remove(uuid)
            }

            if (chr.client.storage.items.values.stream()
                    .anyMatch { item: ItemSlot -> Arrays.equals(item.uuid, uuid) }
            ) {
                keep.add(uuid)
                toDelete.remove(uuid)
            }
        }
        toDelete.forEach(ItemAPI::deleteItemByUUIDOld)
        return keep
    }*/

    /*@Deprecated("old system")
    fun deleteItemByUUIDOld(uuid: ByteArray?) {
        connection.deleteFrom(INVENTORIES)
            .where(INVENTORIES.ID.eq(uuid))
            .execute()
    }*/

    /*@Deprecated("old system")
    private fun updateExistingItem(slot: Short, item: ItemSlot, storageType: Int) {
        val quantity = if (item is ItemSlotBundle) item.number.toInt() else 1
        connection.update(INVENTORIES)
            .set(INVENTORIES.STORAGE_TYPE, storageType)
            .set(INVENTORIES.POSITION, slot)
            .set(INVENTORIES.QUANTITY, quantity)
            .where(INVENTORIES.ID.eq(item.uuid))
            .execute()
    }*/

    /*@Deprecated("old system")
    private fun updateExistingEquip(equip: ItemSlotEquip) {
        connection.update(EQUIPS)
            .set(EQUIPS.SLOTS, equip.ruc)
            .set(EQUIPS.STR, equip.str)
            .set(EQUIPS.DEX, equip.dex)
            .set(EQUIPS.INT, equip.int)
            .set(EQUIPS.LUK, equip.luk)
            .set(EQUIPS.HP, equip.maxHP)
            .set(EQUIPS.MP, equip.maxMP)
            .set(EQUIPS.PAD, equip.pad)
            .set(EQUIPS.MAD, equip.mad)
            .set(EQUIPS.PDD, equip.pdd)
            .set(EQUIPS.MDD, equip.mdd)
            .set(EQUIPS.ACC, equip.acc)
            .set(EQUIPS.EVA, equip.eva)
            .set(EQUIPS.SPEED, equip.speed)
            .set(EQUIPS.JUMP, equip.jump)
            .set(EQUIPS.CRAFT, equip.craft)
            .set(EQUIPS.DURABILITY, equip.durability)
            .where(EQUIPS.ITEMID.eq(equip.uuid))
            .execute()
    }*/

    /*@Deprecated("old system")
    private fun insertNewItemOld(
        chr: Character,
        storageType: Int,
        type: Int,
        slot: Short,
        item: ItemSlot,
        giftFrom: String?
    ) {
        insertNewItemOld(chr.id, chr.client.accId, storageType, type, slot, item, giftFrom)
    }*/

    /*@Deprecated("old system")
    private fun insertNewItemOld(
        cid: Int,
        aid: Int,
        storageType: Int,
        type: Int,
        slot: Short,
        item: ItemSlot,
        giftFrom: String?
    ) {
        try {
            item.uuid ?: run {
                val uuid = HexTool.toBytes(UUID.randomUUID().toString().replace("-", ""))
                item.uuid = uuid
            }

            val quantity = if (item is ItemSlotBundle) item.number.toInt() else 1
            connection.insertInto(
                INVENTORIES,
                INVENTORIES.ID,
                INVENTORIES.CID,
                INVENTORIES.STORAGE_TYPE,
                INVENTORIES.AID,
                INVENTORIES.ITEMID,
                INVENTORIES.INVENTORY_TYPE,
                INVENTORIES.POSITION,
                INVENTORIES.QUANTITY,
                INVENTORIES.OWNER,
                INVENTORIES.GIFTFROM
            )
                .values(
                    item.uuid,
                    cid,
                    storageType,
                    aid,
                    item.templateId,
                    type,
                    slot,
                    quantity,
                    null,
                    giftFrom
                )
                .execute()
        } catch (dae: DataAccessException) {
            log(LogType.HACK, "Duplicate UUID for $cid on $item", this)
            dae.printStackTrace()
        }
    }*/

    /*@Deprecated("old system")
    private fun insertNewEquipOld(equip: ItemSlotEquip) {
        connection.insertInto(
            EQUIPS,
            EQUIPS.ITEMID,
            EQUIPS.SLOTS,
            EQUIPS.LEVEL,
            EQUIPS.STR,
            EQUIPS.DEX,
            EQUIPS.INT,
            EQUIPS.LUK,
            EQUIPS.HP,
            EQUIPS.MP,
            EQUIPS.PAD,
            EQUIPS.MAD,
            EQUIPS.PDD,
            EQUIPS.MDD,
            EQUIPS.ACC,
            EQUIPS.EVA,
            EQUIPS.SPEED,
            EQUIPS.JUMP,
            EQUIPS.CRAFT,
            EQUIPS.DURABILITY
        )
            .values(
                equip.uuid,
                equip.ruc,
                equip.level,
                equip.str,
                equip.dex,
                equip.int,
                equip.luk,
                equip.maxHP,
                equip.maxMP,
                equip.pad,
                equip.mad,
                equip.pdd,
                equip.mdd,
                equip.acc,
                equip.eva,
                equip.speed,
                equip.jump,
                equip.craft,
                equip.durability
            )
            .execute()
    }*/

    /**
     * Loads the entire inventory of a player, should only be used on initial load
     *
     * @param chr Player to load
     */
    /*@Deprecated("old system")
    fun loadInventoriesOld(chr: Character) {
        deleteBrokenEquips(chr)
        loadEquips(chr)
        loadLocker(chr.client)
        loadStorage(chr.client)
        val itemData = connection
            .select()
            .from(INVENTORIES)
            .where(INVENTORIES.STORAGE_TYPE.eq(1))
            .and(INVENTORIES.INVENTORY_TYPE.notEqual(1)) // equip inv
            .and(INVENTORIES.CID.eq(chr.id))
            .fetch()
        itemData.forEach {
            val items = chr.getInventory(it.getValue(INVENTORIES.INVENTORY_TYPE) - 1).items
            val template = ItemManager.getItem(it.getValue(INVENTORIES.ITEMID)) as ItemBundleTemplate
            val bundle = template.toItemSlot()

            if (it.getValue(INVENTORIES.QUANTITY) > 1) {
                bundle.number = it.getValue(INVENTORIES.QUANTITY).toShort()
            }

            bundle.uuid = it.getValue(INVENTORIES.ID)
            bundle.isNewItem = false
            bundle.updated = false
            items[it.getValue(INVENTORIES.POSITION)] = bundle
        }
    }*/

    /*private fun loadStorage(c: Client) {
        val itemStorage = connection.select()
            .from(STORAGES)
            .where(STORAGES.AID.eq(c.accId))
            .fetchOne()?.let {
                ItemStorage(it.getValue(STORAGES.SIZE), it.getValue(STORAGES.MESO))
            }
            ?: run {
                connection.insertInto(STORAGES, STORAGES.AID)
                    .values(c.accId)
                    .execute()
                ItemStorage(4, 0)
            }

        c.storage = itemStorage

        val itemData = connection.select()
            .from(INVENTORIES)
            .where(INVENTORIES.STORAGE_TYPE.eq(2))
            .and(INVENTORIES.AID.eq(c.accId))
            .fetch()

        itemData.forEach {
            ItemManager.getItem(it.getValue(INVENTORIES.ITEMID)).let { template ->
                val item = template.toItemSlot()

                if (it.getValue(INVENTORIES.QUANTITY) > 1) {
                    (item as ItemSlotBundle).number = it.getValue(INVENTORIES.QUANTITY).toShort()
                }

                item.uuid = it.getValue(INVENTORIES.ID)
                item.isNewItem = false
                item.updated = false
                c.storage.items[it.getValue(INVENTORIES.POSITION)] = item
            }
        }
    }*/

    /**
     * If an equip happens to appear in the DB without any stats, it could cause problems.
     * This method detects equips in the DB that have no stats and removes them.
     *
     * @param chr The player to check
     */
    /*@Deprecated("old system")
    private fun deleteBrokenEquips(chr: Character) {
        val itemData = connection.select()
            .from(INVENTORIES)
            .where(INVENTORIES.INVENTORY_TYPE.eq(1)) // equip inv
            .and(INVENTORIES.CID.eq(chr.id))
            .fetch()

        itemData.forEach {
            connection.select()
                .from(EQUIPS)
                .where(EQUIPS.ITEMID.eq(it.getValue(INVENTORIES.ID)))
                .fetchOne()
                ?: run {
                    log(LogType.INVALID, "Broken equip found (${it.getValue(INVENTORIES.ITEMID)})", this, chr.client)
                    deleteItemByUUIDOld(it.getValue(INVENTORIES.ID))
                }
        }
    }*/

    /**
     * Separate from loadInventories because of equip stats
     *
     * @param chr Player to load
     */
    /*@Deprecated("old system")
    private fun loadEquips(chr: Character) {
        val equips = chr.getInventory(ItemInventoryType.EQUIP).items
        val equipData = connection.select()
            .from(EQUIPS)
            .join(INVENTORIES)
            .onKey()
            .where(INVENTORIES.STORAGE_TYPE.eq(1))
            .and(INVENTORIES.INVENTORY_TYPE.eq(1)) // equip inv
            .and(INVENTORIES.CID.eq(chr.id))
            .fetch()

        for (rec in equipData) {
            val template = ItemManager.getItem(rec.getValue(INVENTORIES.ITEMID)) as ItemEquipTemplate
            val equip = template.fromDbToSlot(
                rec.getValue(EQUIPS.SLOTS),
                rec.getValue(EQUIPS.STR),
                rec.getValue(EQUIPS.DEX),
                rec.getValue(EQUIPS.INT),
                rec.getValue(EQUIPS.LUK),
                rec.getValue(EQUIPS.HP),
                rec.getValue(EQUIPS.MP),
                rec.getValue(EQUIPS.PAD),
                rec.getValue(EQUIPS.MAD),
                rec.getValue(EQUIPS.PDD),
                rec.getValue(EQUIPS.MDD),
                rec.getValue(EQUIPS.ACC),
                rec.getValue(EQUIPS.EVA),
                rec.getValue(EQUIPS.SPEED),
                rec.getValue(EQUIPS.JUMP),
                rec.getValue(EQUIPS.CRAFT),
                rec.getValue(EQUIPS.DURABILITY)
            )
            equip.uuid = rec.getValue(INVENTORIES.ID)
            equip.isNewItem = false
            equip.updated = false
            equips[rec.getValue(INVENTORIES.POSITION)] = equip
        }
    }*/

    /**
     * Used for offline gift sending check
     *
     * @param aid account id
     * @return size of account locker
     */
    fun getLockerSize(aid: Int): Int {
        return connection.fetchCount(
            connection.select().from(ITEMLOCKERS)
                .where(ITEMLOCKERS.AID.eq(aid))
        )
    }

    private fun getAvailableLockerSlot(aid: Int): Int {
        var highest = 1
        connection.select().from(ITEMLOCKERS)
            .innerJoin(ITEMSLOTS).on(ITEMSLOTS.ID.eq(ITEMLOCKERS.SLOTID))
            .where(ITEMLOCKERS.AID.eq(aid))
            .fetch()
            .forEach {
                val slot = it.getValue(ITEMSLOTS.POSITION)
                if (highest < slot) {
                    highest = slot + 1
                }
            }
        return highest
    }

    /**
     * Cashshop inventory
     *
     * @param c client
     */
    /*private fun loadLocker(c: Client) {
        val locker = connection
            .select().from(INVENTORIES)
            .where(INVENTORIES.STORAGE_TYPE.eq(3))
            .and(INVENTORIES.AID.eq(c.accId))
            .fetch()

        val toAdd: MutableMap<Short, ItemSlotLocker> = TreeMap()

        locker.forEach {
            val itemId = it.getValue(INVENTORIES.ITEMID)
            ItemManager.getItem(itemId).let { template ->
                val giftFrom = it.getValue(INVENTORIES.GIFTFROM) ?: ""
                val lockerItem = ItemSlotLocker(template.toItemSlot())

                lockerItem.item.uuid = it.getValue(INVENTORIES.ID)
                lockerItem.buyCharacterName = giftFrom
                toAdd[it.getValue(INVENTORIES.POSITION)] = lockerItem
            }
        }

        c.locker.addAll(toAdd.values)
    }*/

    fun addLockerItem(cid: Int, aid: Int, item: ItemSlotLocker) {
        insertNewItemSlot(cid, aid, item.item, getAvailableLockerSlot(aid).toShort(), DBInventoryType.LOCKER)
    }

    /*fun addLockerItem(c: Client, item: ItemSlotLocker) {
        val type = item.item.templateId / 1000000
        insertNewItemOld(c.character, 3, type, (c.locker.indexOf(item) + 1).toShort(), item.item, item.buyCharacterName)

        if (ItemInventoryType.values()[type - 1] == ItemInventoryType.EQUIP) {
            insertNewEquipOld(item.item as ItemSlotEquip)
        }
    }*/

    /*fun moveLockerToStorage(item: ItemSlotLocker, slot: Short) {
        connection.update(INVENTORIES)
            .set(INVENTORIES.STORAGE_TYPE, 1)
            .set(INVENTORIES.POSITION, slot)
            .where(INVENTORIES.ID.eq(item.item.uuid))
            .execute()
    }*/
}