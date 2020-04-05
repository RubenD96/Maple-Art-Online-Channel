package client.inventory.item.templates;

import lombok.Getter;
import util.packet.PacketReader;

public class PortalScrollItemTemplate extends ItemBundleTemplate {

    @Getter private int moveTo;

    public PortalScrollItemTemplate(int id, PacketReader r) {
        super(id, r);
        moveTo = r.readInteger();
    }
}
