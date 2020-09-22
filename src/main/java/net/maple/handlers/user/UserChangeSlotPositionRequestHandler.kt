package net.maple.handlers.user

import client.Client
import client.inventory.ItemInventoryType
import client.inventory.slots.ItemSlotBundle
import constants.ItemConstants.isTreatSingly
import field.obj.drop.ItemDrop
import net.database.ItemAPI.deleteItemByUUID
import net.maple.handlers.PacketHandler
import net.maple.packets.CharacterPackets.modifyInventory
import util.packet.PacketReader

class UserChangeSlotPositionRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        reader.readInteger()

        val type = ItemInventoryType.values()[reader.readByte() - 1]
        val from = reader.readShort()
        val to = reader.readShort()
        val number = reader.readShort()

        if (to.toInt() == 0) { // drop
            chr.modifyInventory({
                val inventory = chr.inventories[type] ?: return@modifyInventory
                var item = inventory.items[from] ?: return@modifyInventory
                val uuid = item.uuid

                if (!isTreatSingly(item.templateId)) {
                    if (item !is ItemSlotBundle) return@modifyInventory
                    if (item.number < number) return@modifyInventory

                    item = it.getInventoryContext(type).take(from, number)
                    item.uuid = uuid
                    deleteItemByUUID(uuid) // clear db entry
                } else {
                    it.getInventoryContext(type).remove(item)
                }

                val drop = ItemDrop(chr.id, chr, item, 0)
                drop.position = chr.position
                drop.expire = System.currentTimeMillis() + 600000
                drop.field = chr.field
                chr.field.enter(drop)
            }, true)
        } else {
            chr.modifyInventory({ it.getInventoryContext(type).move(from, to) }, true)
        }
    }
}