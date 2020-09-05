package client.inventory.slots;

import client.Client;
import constants.ItemConstants;
import util.packet.PacketWriter;

import java.util.Random;

public class ItemSlotLocker {

    private final ItemSlot item;
    private int commodityId, paybackRate, discountRate;
    private String buyCharacterName;

    public ItemSlot getItem() {
        return item;
    }

    public int getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(int commodityId) {
        this.commodityId = commodityId;
    }

    public int getPaybackRate() {
        return paybackRate;
    }

    public void setPaybackRate(int paybackRate) {
        this.paybackRate = paybackRate;
    }

    public int getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(int discountRate) {
        this.discountRate = discountRate;
    }

    public String getBuyCharacterName() {
        return buyCharacterName;
    }

    public void setBuyCharacterName(String buyCharacterName) {
        this.buyCharacterName = buyCharacterName;
    }

    public ItemSlotLocker(ItemSlot item) {
        this.item = item;
        item.setCashItemSN(new Random().nextInt(Integer.MAX_VALUE));
    }

    public void encode(Client c, PacketWriter pw) {
        pw.writeLong(item.getCashItemSN());
        pw.writeInt(c.getAccId());
        pw.writeInt(c.getCharacter().getId());
        pw.writeInt(item.getTemplateId());
        pw.writeInt(commodityId);
        pw.writeShort(item instanceof ItemSlotBundle ? ((ItemSlotBundle) item).getNumber() : 1);
        pw.writeFixedString(buyCharacterName, 13);
        pw.writeLong(ItemConstants.PERMANENT); // todo?
        pw.writeInt(paybackRate);
        pw.writeInt(discountRate);
    }
}
