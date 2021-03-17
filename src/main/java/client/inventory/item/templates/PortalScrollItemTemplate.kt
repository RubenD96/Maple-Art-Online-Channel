package client.inventory.item.templates

import util.packet.PacketReader

class PortalScrollItemTemplate(id: Int) : ItemBundleTemplate(id) {

    var moveTo: Int = 0
        private set

    override fun decode(r: PacketReader): PortalScrollItemTemplate {
        super.decode(r)

        moveTo = r.readInteger()

        return this
    }
}