package net.database

import client.Character
import client.Client
import client.interaction.storage.ItemStorage
import client.inventory.ItemInventory
import client.inventory.ItemInventoryType
import client.inventory.item.slots.*
import client.pet.FieldUserPet
import database.jooq.Tables.*
import managers.ItemManager
import net.database.DatabaseCore.connection
import org.jooq.Record
import util.HexTool
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

                if (item is ItemSlotPet) {
                    if (item.equipSlot.toInt() != -1) {
                        val pet = FieldUserPet(chr, item)
                        pet.idx = item.equipSlot
                        chr.pets.add(pet)
                        item.updated = false
                    }
                }
            }
            chr.pets.sortBy { it.idx }
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
            item.equipSlot = rec.getValue(EQUIPSLOT)
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
        println("Update me!! $item | $position")

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
                .set(EQUIPSLOT, item.equipSlot)
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
        println("Insert me!! $item | $position")
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
                REMAINLIFE,
                EQUIPSLOT
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
                    item.remainLife,
                    item.equipSlot
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
                storageContains(chr.client.storage, it, toUpdate) ->
                    connection.insertInto(ITEMSTORAGES, ITEMSTORAGES.SLOTID, ITEMSTORAGES.AID)
                        .values(it, chr.client.accId)
                        .execute()
                lockerContains(chr.client.locker, it, toUpdate) ->
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
                invContains(chr.allInventories.values, it, toUpdate) ->
                    connection.insertInto(ITEMINVENTORIES, ITEMINVENTORIES.SLOTID, ITEMINVENTORIES.CID)
                        .values(it, chr.id)
                        .execute()
                lockerContains(chr.client.locker, it, toUpdate) ->
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
                invContains(chr.allInventories.values, it, toUpdate) ->
                    connection.insertInto(ITEMINVENTORIES, ITEMINVENTORIES.SLOTID, ITEMINVENTORIES.CID)
                        .values(it, chr.id)
                        .execute()
                storageContains(chr.client.storage, it, toUpdate) ->
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

    private fun invContains(
        inventories: Collection<ItemInventory>,
        uuid: ByteArray,
        toUpdate: MutableSet<ItemSlot>
    ): Boolean {
        inventories.forEach { inv ->
            val item = inv.items.values.firstOrNull { it.uuid.contentEquals(uuid) }
            if (item != null) {
                toUpdate.add(item)
                return true
            }
        }
        return false
    }

    private fun storageContains(storage: ItemStorage, uuid: ByteArray, toUpdate: MutableSet<ItemSlot>): Boolean {
        val item = storage.items.values.firstOrNull { it.uuid.contentEquals(uuid) }?.also { toUpdate.add(it) }
        return item != null
    }

    private fun lockerContains(locker: List<ItemSlotLocker>, uuid: ByteArray, toUpdate: MutableSet<ItemSlot>): Boolean {
        val item = locker.firstOrNull { it.item.uuid.contentEquals(uuid) }?.also { toUpdate.add(it.item) }
        return item != null
    }

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

    fun addLockerItem(cid: Int, aid: Int, item: ItemSlotLocker) {
        insertNewItemSlot(cid, aid, item.item, getAvailableLockerSlot(aid).toShort(), DBInventoryType.LOCKER)
    }
}