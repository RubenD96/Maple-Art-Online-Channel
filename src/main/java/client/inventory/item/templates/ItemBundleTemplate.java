package client.inventory.item.templates;

import client.inventory.slots.ItemSlotBundle;
import util.packet.PacketReader;

public class ItemBundleTemplate extends ItemTemplate {

    private double unitPrice;
    private short maxPerSlot;

    public double getUnitPrice() {
        return unitPrice;
    }

    public short getMaxPerSlot() {
        return maxPerSlot;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setMaxPerSlot(short maxPerSlot) {
        this.maxPerSlot = maxPerSlot;
    }

    public ItemBundleTemplate(int id, PacketReader r) {
        super(id, r);
        unitPrice = r.readDouble();
        maxPerSlot = r.readShort();
        if (maxPerSlot == 0) {
            maxPerSlot = 100;
        }
    }

    public ItemSlotBundle toItemSlot() {
        ItemSlotBundle item = new ItemSlotBundle();
        item.setTemplateId(getId());
        item.setNumber((short) 1);
        item.setMaxNumber(maxPerSlot);
        return item;
    }

    @Override
    public String toString() {
        System.out.println(super.toString());
        return "ItemBundleTemplate{" +
                "unitPrice=" + unitPrice +
                ", maxPerSlot=" + maxPerSlot +
                '}';
    }
}
