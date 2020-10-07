package client.inventory.item.templates

import util.packet.PacketReader

class
PortalScrollItemTemplate(id: Int, r: PacketReader) : ItemBundleTemplate(id, r) {
    val moveTo: Int

    init {
        moveTo = r.readInteger()
    }
}