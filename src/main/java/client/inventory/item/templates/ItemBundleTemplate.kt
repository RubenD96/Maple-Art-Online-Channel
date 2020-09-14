package client.inventory.item.templates

import client.inventory.slots.ItemSlotBundle
import util.packet.PacketReader

open class ItemBundleTemplate(id: Int, r: PacketReader) : ItemTemplate(id, r) {

    val unitPrice: Double
    var maxPerSlot: Short

    override fun toItemSlot(): ItemSlotBundle {
        val item = ItemSlotBundle()
        item.templateId = id
        item.number = 1.toShort()
        item.maxNumber = maxPerSlot
        return item
    }

    init {
        unitPrice = r.readDouble()
        maxPerSlot = r.readShort()
        if (maxPerSlot.toInt() == 0) {
            maxPerSlot = 100
        }
    }
}