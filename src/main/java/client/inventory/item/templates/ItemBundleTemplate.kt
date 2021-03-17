package client.inventory.item.templates

import client.inventory.item.slots.ItemSlotBundle
import util.packet.PacketReader

open class ItemBundleTemplate(id: Int) : ItemTemplate(id) {

    var unitPrice: Double = 0.0
        private set
    var maxPerSlot: Short = 0
        private set

    override fun toItemSlot(): ItemSlotBundle {
        val item = ItemSlotBundle()
        item.templateId = id
        item.number = 1.toShort()
        item.maxNumber = maxPerSlot
        return item
    }

    override fun decode(r: PacketReader): ItemBundleTemplate {
        super.decode(r)

        unitPrice = r.readDouble()
        maxPerSlot = r.readShort()
        if (maxPerSlot.toInt() == 0) {
            maxPerSlot = 100
        }

        return this
    }
}