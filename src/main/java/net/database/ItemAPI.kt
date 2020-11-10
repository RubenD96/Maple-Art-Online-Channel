package net.database

import client.Character
import client.Client
import client.interaction.storage.ItemStorage
import client.inventory.ItemInventoryType
import client.inventory.item.slots.ItemSlot
import client.inventory.item.slots.ItemSlotBundle
import client.inventory.item.slots.ItemSlotEquip
import client.inventory.item.slots.ItemSlotLocker
import client.inventory.item.templates.ItemBundleTemplate
import client.inventory.item.templates.ItemEquipTemplate
import database.jooq.Tables.*
import managers.ItemManager
import net.database.DatabaseCore.connection
import org.jooq.exception.DataAccessException
import util.HexTool
import util.logging.LogType
import util.logging.Logger.log
import java.util.*
import java.util.stream.IntStream
import kotlin.collections.HashSet

/**
 * TODO CHECK:
    1. player 1 loads existing item from db (so already has uuid etc)
    2. player 1 drops item
    3. player 2 picks it up
    4. player 2 gets saved
 */
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
            inv.forEach { (slot: Short, item: ItemSlot) ->
                updateItem(chr, item, uuids, slot, type, 1)
            }
        }

        // storage
        updateStorageStats(chr.client)
        chr.client.storage.items.forEach { (slot: Short, item: ItemSlot) ->
            updateItem(chr, item, uuids, slot, ItemInventoryType.values()[item.templateId / 1000000 - 1], 2)
        }
    }

    private fun updateItem(chr: Character, item: ItemSlot, uuids: Set<ByteArray>, slot: Short, invType: ItemInventoryType, storageType: Int) {
        if (item.uuid == null || item.isNewItem && uuids.stream().noneMatch { uuid -> Arrays.equals(item.uuid, uuid) }) { // item is new, insert new entry
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
        connection.update(STORAGES)
                .set(STORAGES.SIZE, storage.slotMax)
                .set(STORAGES.MESO, storage.meso)
                .where(STORAGES.AID.eq(c.accId))
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

            if (chr.client.storage.items.values.stream().anyMatch { item: ItemSlot -> Arrays.equals(item.uuid, uuid) }) {
                keep.add(uuid)
                toDelete.remove(uuid)
            }
        }
        toDelete.forEach(ItemAPI::deleteItemByUUID)
        return keep
    }

    fun deleteItemByUUID(uuid: ByteArray?) {
        connection.deleteFrom(INVENTORIES)
                .where(INVENTORIES.ID.eq(uuid))
                .execute()
    }

    private fun updateExistingItem(slot: Short, item: ItemSlot, storageType: Int) {
        val quantity = if (item is ItemSlotBundle) item.number.toInt() else 1
        connection.update(INVENTORIES)
                .set(INVENTORIES.STORAGE_TYPE, storageType)
                .set(INVENTORIES.POSITION, slot)
                .set(INVENTORIES.QUANTITY, quantity)
                .where(INVENTORIES.ID.eq(item.uuid))
                .execute()
    }

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
    }

    private fun insertNewItem(chr: Character, storageType: Int, type: Int, slot: Short, item: ItemSlot, giftFrom: String?) {
        insertNewItem(chr.id, chr.client.accId, storageType, type, slot, item, giftFrom)
    }

    private fun insertNewItem(cid: Int, aid: Int, storageType: Int, type: Int, slot: Short, item: ItemSlot, giftFrom: String?) {
        try {
            item.uuid ?: run {
                val uuid = HexTool.toBytes(UUID.randomUUID().toString().replace("-", ""))
                item.uuid = uuid
            }

            val quantity = if (item is ItemSlotBundle) item.number.toInt() else 1
            connection.insertInto(INVENTORIES,
                    INVENTORIES.ID,
                    INVENTORIES.CID,
                    INVENTORIES.STORAGE_TYPE,
                    INVENTORIES.AID,
                    INVENTORIES.ITEMID,
                    INVENTORIES.INVENTORY_TYPE,
                    INVENTORIES.POSITION,
                    INVENTORIES.QUANTITY,
                    INVENTORIES.OWNER,
                    INVENTORIES.GIFTFROM)
                    .values(item.uuid,
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
            log(LogType.HACK, "Duplicate UUID for $cid on $item", this)
            dae.printStackTrace()
        }
    }

    private fun insertNewEquip(equip: ItemSlotEquip) {
        connection.insertInto(EQUIPS,
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
                EQUIPS.DURABILITY)
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
            items[it.getValue(INVENTORIES.POSITION)] = bundle
        }
    }

    private fun loadStorage(c: Client) {
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
                    ItemStorage(4.toShort(), 0)
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
                c.storage.items[it.getValue(INVENTORIES.POSITION)] = item
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
                        deleteItemByUUID(it.getValue(INVENTORIES.ID))
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
            equips[rec.getValue(INVENTORIES.POSITION)] = equip
        }
    }

    /**
     * Used for offline gift sending check
     *
     * @param aid account id
     * @return size of account locker
     */
    fun getLockerSize(aid: Int): Int {
        return connection.fetchCount(
                connection.select().from(INVENTORIES)
                        .where(INVENTORIES.STORAGE_TYPE.eq(3))
                        .and(INVENTORIES.AID.eq(aid)))
    }

    private fun getAvailableLockerSlot(aid: Int): Int {
        var highest = 1
        connection.select().from(INVENTORIES)
                .where(INVENTORIES.STORAGE_TYPE.eq(3))
                .and(INVENTORIES.AID.eq(aid))
                .fetch()
                .forEach {
                    val slot = it.getValue(INVENTORIES.POSITION)
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
    private fun loadLocker(c: Client) {
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
        connection.update(INVENTORIES)
                .set(INVENTORIES.STORAGE_TYPE, 1)
                .set(INVENTORIES.POSITION, slot)
                .where(INVENTORIES.ID.eq(item.item.uuid))
                .execute()
    }
}