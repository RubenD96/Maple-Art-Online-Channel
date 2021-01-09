package net.maple.handlers.item

import client.Client
import client.inventory.ItemInventoryType
import client.inventory.item.slots.ItemSlot
import client.inventory.item.templates.ItemTemplate
import managers.ItemManager
import net.maple.handlers.PacketHandler
import util.packet.PacketReader

abstract class AbstractItemUseHandler<T : ItemTemplate> : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        reader.readInteger() // timestamp?

        val pos = reader.readShort()
        val id = reader.readInteger()
        val type = ItemInventoryType.values()[id / 1000000 - 1]
        val template = ItemManager.getItem(id)

        if (type != ItemInventoryType.CONSUME) return // not needed?
        //if (template !is T) return

        val inv = c.character.getInventory(type)
        val item = inv.items[pos] ?: return

        if (item.templateId != id) return

        handlePacket(reader, c, template as T, item)
    }

    abstract fun handlePacket(reader: PacketReader, c: Client, template: T, item: ItemSlot)
}