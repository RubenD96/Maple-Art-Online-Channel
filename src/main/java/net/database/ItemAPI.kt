package net.database

import client.Character
import client.Client
import client.interaction.storage.ItemStorage
import client.inventory.ItemInventoryType
import client.inventory.item.templates.ItemBundleTemplate
import client.inventory.item.templates.ItemEquipTemplate
import client.inventory.item.slots.ItemSlot
import client.inventory.item.slots.ItemSlotBundle
import client.inventory.item.slots.ItemSlotEquip
import client.inventory.item.slots.ItemSlotLocker
import database.jooq.Tables
import managers.ItemManager
import net.database.DatabaseCore.connection
import org.jooq.Record
import org.jooq.exception.DataAccessException
import util.HexTool
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import java.util.stream.IntStream
import kotlin.collections.HashSet

object ItemAPI {
    /**
     * Saves the player's inventory.
     * Only insert if an new item has entered the player's inventory.
     * Only update if an item already existed and has been changed.
     *
     * @param chr The player to save
     */
    fun saveInventories(chr: Character) {
        val uuids = deleteOldItems(chr)

        // inventories
        IntStream.range(0, 5).forEach {
            val type = ItemInventoryType.values()[it]
            val inv = chr.getInventory(type).items
            inv.forEach { (slot: Short, item: ItemSlot) -> updateItem(chr, item, uuids, slot, type, 1) }
        }

        // storage
        updateStorageStats(chr.client)
        chr.client.storage.items.forEach { (slot: Short, item: ItemSlot) -> updateItem(chr, item, uuids, slot, ItemInventoryType.values()[item.templateId / 1000000 - 1], 2) }
    }

    private fun updateItem(chr: Character, item: ItemSlot, uuids: Set<ByteArray>, slot: Short, invType: ItemInventoryType, storageType: Int) {
        if (item.uuid == null || item.isNewItem && uuids.stream().noneMatch { uuid: ByteArray? -> Arrays.equals(item.uuid, uuid) }) { // item is new, insert new entry
            insertNewItem(chr, storageType, invType.type, slot, item, null)
            if (invType == ItemInventoryType.EQUIP) {
                insertNewEquip(item as ItemSlotEquip)
            }
        } else {
            updateExistingItem(slot, item, storageType)
            if (invType == ItemInventoryType.EQUIP) {
                updateExistingEquip(item as ItemSlotEquip)
            }
        }
    }

    /**
     * Update meso and storage size
     *
     * @param c account to update
     */
    private fun updateStorageStats(c: Client) {
        val storage = c.storage
        connection.update(Tables.STORAGES)
                .set(Tables.STORAGES.SIZE, storage.slotMax)
                .set(Tables.STORAGES.MESO, storage.meso)
                .where(Tables.STORAGES.AID.eq(c.accId))
                .execute()
    }

    /**
     * Deletes old items that are no longer in the player's inventory
     *
     * @param chr The player to remove items from
     */
    private fun deleteOldItems(chr: Character): Set<ByteArray> {
        val toDelete: MutableSet<ByteArray> = HashSet()
        val keep: MutableSet<ByteArray> = HashSet()
        val res = connection
                .select(Tables.INVENTORIES.ID)
                .from(Tables.INVENTORIES)
                .where(Tables.INVENTORIES.CID.eq(chr.id))
                .fetch()
        for (id in res) {
            val uuid = id.getValue(Tables.INVENTORIES.ID)
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

            if (chr.client.storage.items.values.stream().anyMatch { item: ItemSlot -> Arrays.equals(item.uuid, uuid) }) {
                keep.add(uuid)
                toDelete.remove(uuid)
            }
        }
        toDelete.forEach(ItemAPI::deleteItemByUUID)
        return keep
    }

    fun deleteItemByUUID(uuid: ByteArray?) {
        connection
                .deleteFrom(Tables.INVENTORIES)
                .where(Tables.INVENTORIES.ID.eq(uuid))
                .execute()
    }

    private fun updateExistingItem(slot: Short, item: ItemSlot, storageType: Int) {
        val quantity = if (item is ItemSlotBundle) item.number.toInt() else 1
        connection
                .update(Tables.INVENTORIES)
                .set(Tables.INVENTORIES.STORAGE_TYPE, storageType)
                .set(Tables.INVENTORIES.POSITION, slot)
                .set(Tables.INVENTORIES.QUANTITY, quantity)
                .where(Tables.INVENTORIES.ID.eq(item.uuid))
                .execute()
    }

    private fun updateExistingEquip(equip: ItemSlotEquip) {
        connection
                .update(Tables.EQUIPS)
                .set(Tables.EQUIPS.SLOTS, equip.ruc)
                .set(Tables.EQUIPS.STR, equip.str)
                .set(Tables.EQUIPS.DEX, equip.dex)
                .set(Tables.EQUIPS.INT, equip.int)
                .set(Tables.EQUIPS.LUK, equip.luk)
                .set(Tables.EQUIPS.HP, equip.maxHP)
                .set(Tables.EQUIPS.MP, equip.maxMP)
                .set(Tables.EQUIPS.PAD, equip.pad)
                .set(Tables.EQUIPS.MAD, equip.mad)
                .set(Tables.EQUIPS.PDD, equip.pdd)
                .set(Tables.EQUIPS.MDD, equip.mdd)
                .set(Tables.EQUIPS.ACC, equip.acc)
                .set(Tables.EQUIPS.EVA, equip.eva)
                .set(Tables.EQUIPS.SPEED, equip.speed)
                .set(Tables.EQUIPS.JUMP, equip.jump)
                .set(Tables.EQUIPS.CRAFT, equip.craft)
                .set(Tables.EQUIPS.DURABILITY, equip.durability)
                .where(Tables.EQUIPS.ITEMID.eq(equip.uuid))
                .execute()
    }

    private fun insertNewItem(chr: Character, storageType: Int, type: Int, slot: Short, item: ItemSlot, giftFrom: String?) {
        insertNewItem(chr.id, chr.client.accId, storageType, type, slot, item, giftFrom)
    }

    private fun insertNewItem(cid: Int, aid: Int, storageType: Int, type: Int, slot: Short, item: ItemSlot, giftFrom: String?) {
        try {
            var uuid = item.uuid
            if (uuid == null) {
                uuid = HexTool.toBytes(UUID.randomUUID().toString().replace("-", ""))
                item.uuid = uuid
            }
            val quantity = if (item is ItemSlotBundle) item.number.toInt() else 1
            connection
                    .insertInto(Tables.INVENTORIES,
                            Tables.INVENTORIES.ID,
                            Tables.INVENTORIES.CID,
                            Tables.INVENTORIES.STORAGE_TYPE,
                            Tables.INVENTORIES.AID,
                            Tables.INVENTORIES.ITEMID,
                            Tables.INVENTORIES.INVENTORY_TYPE,
                            Tables.INVENTORIES.POSITION,
                            Tables.INVENTORIES.QUANTITY,
                            Tables.INVENTORIES.OWNER,
                            Tables.INVENTORIES.GIFTFROM)
                    .values(uuid,
                            cid,
                            storageType,
                            aid,
                            item.templateId,
                            type,
                            slot,
                            quantity,
                            null,
                            giftFrom)
                    .execute()
        } catch (dae: DataAccessException) {
            System.err.println("[ItemAPI] Duplicate UUID for $cid on $item")
            dae.printStackTrace()
        }
    }

    private fun insertNewEquip(equip: ItemSlotEquip) {
        connection
                .insertInto(Tables.EQUIPS,
                        Tables.EQUIPS.ITEMID,
                        Tables.EQUIPS.SLOTS,
                        Tables.EQUIPS.LEVEL,
                        Tables.EQUIPS.STR,
                        Tables.EQUIPS.DEX,
                        Tables.EQUIPS.INT,
                        Tables.EQUIPS.LUK,
                        Tables.EQUIPS.HP,
                        Tables.EQUIPS.MP,
                        Tables.EQUIPS.PAD,
                        Tables.EQUIPS.MAD,
                        Tables.EQUIPS.PDD,
                        Tables.EQUIPS.MDD,
                        Tables.EQUIPS.ACC,
                        Tables.EQUIPS.EVA,
                        Tables.EQUIPS.SPEED,
                        Tables.EQUIPS.JUMP,
                        Tables.EQUIPS.CRAFT,
                        Tables.EQUIPS.DURABILITY)
                .values(equip.uuid,
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
                        equip.durability)
                .execute()
    }

    /**
     * Loads the entire inventory of a player, should only be used on initial load
     *
     * @param chr Player to load
     */
    fun loadInventories(chr: Character) {
        deleteBrokenEquips(chr)
        loadEquips(chr)
        loadLocker(chr.client)
        loadStorage(chr.client)
        val itemData = connection
                .select()
                .from(Tables.INVENTORIES)
                .where(Tables.INVENTORIES.STORAGE_TYPE.eq(1))
                .and(Tables.INVENTORIES.INVENTORY_TYPE.notEqual(1)) // equip inv
                .and(Tables.INVENTORIES.CID.eq(chr.id))
                .fetch()
        itemData.forEach(Consumer { rec: Record ->
            val items = chr.getInventory(ItemInventoryType.values()[rec.getValue(Tables.INVENTORIES.INVENTORY_TYPE) - 1]).items
            val template = ItemManager.getItem(rec.getValue(Tables.INVENTORIES.ITEMID)) as ItemBundleTemplate
            val bundle = template.toItemSlot()

            if (rec.getValue(Tables.INVENTORIES.QUANTITY) > 1) {
                bundle.number = rec.getValue(Tables.INVENTORIES.QUANTITY).toShort()
            }

            bundle.uuid = rec.getValue(Tables.INVENTORIES.ID)
            bundle.isNewItem = false
            items[rec.getValue(Tables.INVENTORIES.POSITION)] = bundle
        })
    }

    private fun loadStorage(c: Client) {
        val storage = connection
                .select()
                .from(Tables.STORAGES)
                .where(Tables.STORAGES.AID.eq(c.accId))
                .fetchOne()
        val itemStorage: ItemStorage

        itemStorage = if (storage == null) {
            connection.insertInto(Tables.STORAGES, Tables.STORAGES.AID)
                    .values(c.accId)
                    .execute()
            ItemStorage(4.toShort(), 0)
        } else {
            ItemStorage(storage.getValue(Tables.STORAGES.SIZE), storage.getValue(Tables.STORAGES.MESO))
        }

        c.storage = itemStorage
        val itemData = connection
                .select()
                .from(Tables.INVENTORIES)
                .where(Tables.INVENTORIES.STORAGE_TYPE.eq(2))
                .and(Tables.INVENTORIES.AID.eq(c.accId))
                .fetch()

        itemData.forEach {
            val template = ItemManager.getItem(it.getValue(Tables.INVENTORIES.ITEMID))
            if (template != null) {
                val item = template.toItemSlot()

                if (it.getValue(Tables.INVENTORIES.QUANTITY) > 1) {
                    (item as ItemSlotBundle).number = it.getValue(Tables.INVENTORIES.QUANTITY).toShort()
                }

                item.uuid = it.getValue(Tables.INVENTORIES.ID)
                item.isNewItem = false
                c.storage.items[it.getValue(Tables.INVENTORIES.POSITION)] = item
            }
        }
    }

    /**
     * If an equip happens to appear in the DB without any stats, it could cause problems.
     * This method detects equips in the DB that have no stats and removes them.
     *
     * @param chr The player to check
     */
    private fun deleteBrokenEquips(chr: Character) {
        val itemData = connection.select()
                .from(Tables.INVENTORIES)
                .where(Tables.INVENTORIES.INVENTORY_TYPE.eq(1)) // equip inv
                .and(Tables.INVENTORIES.CID.eq(chr.id))
                .fetch()
        itemData.forEach {
            connection.select()
                    .from(Tables.EQUIPS)
                    .where(Tables.EQUIPS.ITEMID.eq(it.getValue(Tables.INVENTORIES.ID)))
                    .fetchOne()
                    ?: run {
                        System.err.println("[ItemAPI] Broken equip found for ${chr.name} (${it.getValue(Tables.INVENTORIES.ITEMID)})")
                        deleteItemByUUID(it.getValue(Tables.INVENTORIES.ID))
                    }
        }
    }

    /**
     * Separate from loadInventories because of equip stats
     *
     * @param chr Player to load
     */
    private fun loadEquips(chr: Character) {
        val equips = chr.getInventory(ItemInventoryType.EQUIP).items
        val equipData = connection
                .select()
                .from(Tables.EQUIPS)
                .join(Tables.INVENTORIES)
                .onKey()
                .where(Tables.INVENTORIES.STORAGE_TYPE.eq(1))
                .and(Tables.INVENTORIES.INVENTORY_TYPE.eq(1)) // equip inv
                .and(Tables.INVENTORIES.CID.eq(chr.id))
                .fetch()
        for (rec in equipData) {
            val template = ItemManager.getItem(rec.getValue(Tables.INVENTORIES.ITEMID)) as ItemEquipTemplate
            val equip = template.fromDbToSlot(
                    rec.getValue(Tables.EQUIPS.SLOTS),
                    rec.getValue(Tables.EQUIPS.STR),
                    rec.getValue(Tables.EQUIPS.DEX),
                    rec.getValue(Tables.EQUIPS.INT),
                    rec.getValue(Tables.EQUIPS.LUK),
                    rec.getValue(Tables.EQUIPS.HP),
                    rec.getValue(Tables.EQUIPS.MP),
                    rec.getValue(Tables.EQUIPS.PAD),
                    rec.getValue(Tables.EQUIPS.MAD),
                    rec.getValue(Tables.EQUIPS.PDD),
                    rec.getValue(Tables.EQUIPS.MDD),
                    rec.getValue(Tables.EQUIPS.ACC),
                    rec.getValue(Tables.EQUIPS.EVA),
                    rec.getValue(Tables.EQUIPS.SPEED),
                    rec.getValue(Tables.EQUIPS.JUMP),
                    rec.getValue(Tables.EQUIPS.CRAFT),
                    rec.getValue(Tables.EQUIPS.DURABILITY)
            )
            equip.uuid = rec.getValue(Tables.INVENTORIES.ID)
            equip.isNewItem = false
            equips[rec.getValue(Tables.INVENTORIES.POSITION)] = equip
        }
    }

    /**
     * Used for offline gift sending check
     *
     * @param aid account id
     * @return size of account locker
     */
    fun getLockerSize(aid: Int): Int {
        return connection
                .fetchCount(connection
                        .select().from(Tables.INVENTORIES)
                        .where(Tables.INVENTORIES.STORAGE_TYPE.eq(3))
                        .and(Tables.INVENTORIES.AID.eq(aid)))
    }

    private fun getAvailableLockerSlot(aid: Int): Int {
        val highest = AtomicInteger(1)
        connection
                .select().from(Tables.INVENTORIES)
                .where(Tables.INVENTORIES.STORAGE_TYPE.eq(3))
                .and(Tables.INVENTORIES.AID.eq(aid))
                .fetch()
                .forEach(Consumer { item: Record ->
                    val slot = item.getValue(Tables.INVENTORIES.POSITION)
                    if (highest.get() < slot) {
                        highest.set(slot + 1)
                    }
                })
        return highest.get()
    }

    /**
     * Cashshop inventory
     *
     * @param c client
     */
    private fun loadLocker(c: Client) {
        val locker = connection
                .select().from(Tables.INVENTORIES)
                .where(Tables.INVENTORIES.STORAGE_TYPE.eq(3))
                .and(Tables.INVENTORIES.AID.eq(c.accId))
                .fetch()
        val toRemove: MutableList<Int> = ArrayList()
        val toAdd: MutableMap<Short, ItemSlotLocker> = TreeMap()
        locker.forEach(Consumer { item: Record ->
            val itemId = item.getValue(Tables.INVENTORIES.ITEMID)
            val template = ItemManager.getItem(itemId)
            val giftFrom = item.getValue(Tables.INVENTORIES.GIFTFROM)
            if (template != null) { // mmhm
                val lockerItem = ItemSlotLocker(template.toItemSlot())
                lockerItem.item.uuid = item.getValue(Tables.INVENTORIES.ID)
                lockerItem.buyCharacterName = giftFrom ?: ""
                toAdd[item.getValue(Tables.INVENTORIES.POSITION)] = lockerItem
            } else {
                toRemove.add(itemId)
            }
        })
        c.locker.addAll(toAdd.values)
        if (toRemove.isNotEmpty()) {
            val del = connection.deleteFrom(Tables.INVENTORIES)
            var conditionStep = del.where(Tables.INVENTORIES.ITEMID.eq(toRemove[0]))
            toRemove.removeAt(0) // this is pretty derpy...
            for (r in toRemove) {
                conditionStep = conditionStep.or(Tables.INVENTORIES.ITEMID.eq(r))
            }
            del.execute()
        }
    }

    fun addLockerItem(cid: Int, aid: Int, item: ItemSlotLocker) {
        val type = item.item.templateId / 1000000
        insertNewItem(cid, aid, 3, type, getAvailableLockerSlot(aid).toShort(), item.item, item.buyCharacterName)
        if (ItemInventoryType.values()[type - 1] == ItemInventoryType.EQUIP) {
            insertNewEquip(item.item as ItemSlotEquip)
        }
    }

    fun addLockerItem(c: Client, item: ItemSlotLocker) {
        val type = item.item.templateId / 1000000
        insertNewItem(c.character, 3, type, (c.locker.indexOf(item) + 1).toShort(), item.item, item.buyCharacterName)
        if (ItemInventoryType.values()[type - 1] == ItemInventoryType.EQUIP) {
            insertNewEquip(item.item as ItemSlotEquip)
        }
    }

    fun moveLockerToStorage(item: ItemSlotLocker, slot: Short) {
        connection
                .update(Tables.INVENTORIES)
                .set(Tables.INVENTORIES.STORAGE_TYPE, 1)
                .set(Tables.INVENTORIES.POSITION, slot)
                .where(Tables.INVENTORIES.ID.eq(item.item.uuid))
                .execute()
    }
}