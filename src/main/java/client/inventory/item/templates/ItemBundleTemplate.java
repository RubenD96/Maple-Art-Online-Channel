package client.inventory.item.templates;

import client.inventory.slots.ItemSlotBundle;
import lombok.Getter;
import util.packet.PacketReader;

@Getter
public class ItemBundleTemplate extends ItemTemplate {

    private double unitPrice;
    private short maxPerSlot;

    public ItemBundleTemplate(int id, PacketReader r) {
        super(id, r);
        unitPrice = r.readDouble();
        maxPerSlot = r.readShort();
    }

    public ItemSlotBundle toItemSlot() {
        ItemSlotBundle item = new ItemSlotBundle();
        item.setTemplateId(getId());
        item.setNumber((short) 1);
        item.setMaxNumber(maxPerSlot);
        return item;
    }
}
