package client.interaction.storage

import client.Character
import client.Client
import client.interaction.Interactable
import client.inventory.ItemInventoryType
import client.inventory.ModifyInventoryContext
import client.inventory.slots.ItemSlot
import client.inventory.slots.ItemSlotBundle
import client.player.DbChar
import constants.ItemConstants
import net.maple.SendOpcode
import net.maple.packets.CharacterPackets
import net.maple.packets.CharacterPackets.modifyInventory
import net.maple.packets.ItemPackets.encode
import util.packet.PacketWriter
import java.util.*
import java.util.stream.Collectors

class ItemStorageInteraction(val npcId: Int, val storage: ItemStorage) : Interactable {

    override fun open(chr: Character) {
        if (chr.activeStorage != null) {
            chr.client.close(this, "Attempting to open a storage while in a storage")
            return
        }

        val pw = PacketWriter(18) // min size (0 items, 0 meso)

        pw.writeHeader(SendOpcode.STORAGE_RESULT)
        pw.write(StorageResult.OPEN_STORAGE_DLG.value)
        pw.writeInt(npcId)
        encodeItems(pw)

        chr.write(pw.createPacket())
        chr.activeStorage = this
    }

    override fun close(c: Client) {
        c.character.activeStorage = null
    }

    @JvmOverloads
    fun encodeItems(pw: PacketWriter, flags: DbChar = DbChar.ALL) {
        pw.write(storage.slotMax.toInt())
        pw.writeLong(flags.value.toLong())

        if (flags.containsFlag(DbChar.MONEY)) pw.writeInt(storage.meso)

        val types: MutableMap<DbChar, ItemInventoryType> = EnumMap(client.player.DbChar::class.java)
        types[DbChar.ITEM_SLOT_EQUIP] = ItemInventoryType.EQUIP
        types[DbChar.ITEM_SLOT_CONSUME] = ItemInventoryType.CONSUME
        types[DbChar.ITEM_SLOT_INSTALL] = ItemInventoryType.INSTALL
        types[DbChar.ITEM_SLOT_ETC] = ItemInventoryType.ETC
        types[DbChar.ITEM_SLOT_CASH] = ItemInventoryType.CASH

        types.entries.stream()
                .filter { flags.containsFlag(it.key) }
                .forEach {
                    val items = storage.items.values.stream()
                            .filter { itemSlot: ItemSlot -> ItemInventoryType.values()[itemSlot.templateId / 1000000] == it.value }
                            .collect(Collectors.toList())

                    pw.write(items.size)
                    items.forEach { it.encode(pw) }
                }
    }

    fun getItem(chr: Character, type: ItemInventoryType, pos: Short): StorageResult {
        val item = storage.items.values.stream()
                .filter { ItemInventoryType.values()[it.templateId / 1000000 - 1] == type }
                .collect(Collectors.toList())[pos.toInt()]

        if (!chr.hasInvSpace(item)) return StorageResult.GET_HAVING_ONLY_ITEM
        /*if (chr.getMeso() < 100) return StorageResult.GET_NO_MONEY;
        chr.gainMeso(-100);*/

        chr.modifyInventory({ it.add(item) })
        ModifyInventoryContext(storage).remove(item)

        return StorageResult.GET_SUCCESS
    }

    fun storeItem(chr: Character, pos: Short, id: Int, count: Short): StorageResult {
        val inventory = chr.inventories[ItemInventoryType.values()[id / 1000000 - 1]]
                ?: return StorageResult.PUT_UNKNOWN
        var item = inventory.items[pos] ?: return StorageResult.PUT_UNKNOWN

        if (storage.items.size >= storage.slotMax) return StorageResult.PUT_NO_SPACE
        if (chr.meso < 100) return StorageResult.PUT_NO_MONEY
        chr.gainMeso(-100)

        chr.modifyInventory({
            if (!ItemConstants.isTreatSingly(item.templateId) && item is ItemSlotBundle) {
                val bundle = item as ItemSlotBundle
                item = it.take(bundle, if (bundle.number < count) bundle.number else count)
            } else {
                it.remove(item)
            }
        })

        ModifyInventoryContext(storage).add(item)
        return StorageResult.PUT_SUCCESS
    }

    fun sortItems(): StorageResult {
        return StorageResult.SORT_ITEM
    }

    /*
    public StorageResult transferMeso(Character chr, int amount) {
        if (amount < 0 && chr.getMeso() < amount || Integer.MAX_VALUE - chr.getMeso() < amount)
            return StorageResult.PUT_NO_MONEY;
        if (amount > 0 && storage.getMeso() < amount || Integer.MAX_VALUE - storage.getMeso() < amount)
            return StorageResult.GET_NO_MONEY;

        storage.setMeso(storage.getMeso() + -amount);
        chr.gainMeso(amount);

        return StorageResult.MONEY_SUCCESS;
    }
     */
    fun transferMeso(chr: Character, amount: Int): StorageResult {
        if (amount < 0 && chr.meso < amount || Int.MAX_VALUE - chr.meso < amount) return StorageResult.PUT_NO_MONEY
        if (amount > 0 && storage.meso < amount || Int.MAX_VALUE - storage.meso < amount) return StorageResult.GET_NO_MONEY

        storage.meso = storage.meso + -amount
        chr.gainMeso(amount)

        return StorageResult.MONEY_SUCCESS
    }

    enum class StorageResult(val value: Int) {
        GET_SUCCESS(0x9),
        GET_UNKNOWN(0xA),
        GET_NO_MONEY(0xB),
        GET_HAVING_ONLY_ITEM(0xC),
        PUT_SUCCESS(0xD),
        PUT_INCORRECT_REQUEST(0xE),
        SORT_ITEM(0xF),
        PUT_NO_MONEY(0x10),
        PUT_NO_SPACE(0x11),
        PUT_UNKNOWN(0x12),
        MONEY_SUCCESS(0x13),
        MONEY_UNKNOWN(0x14),
        TRUNK_CHECK_SSN_2(0x15),
        OPEN_STORAGE_DLG(0x16),
        TRADE_BLOCKED(0x17),
        SERVER_MSG(0x18);
    }
}