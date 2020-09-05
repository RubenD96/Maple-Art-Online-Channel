package client.inventory.item.templates;

import util.packet.PacketReader;

public class PortalScrollItemTemplate extends ItemBundleTemplate {

    private final int moveTo;

    public int getMoveTo() {
        return moveTo;
    }

    public PortalScrollItemTemplate(int id, PacketReader r) {
        super(id, r);
        moveTo = r.readInteger();
    }
}
