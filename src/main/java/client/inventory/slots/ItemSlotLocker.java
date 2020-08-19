package client.inventory.slots;

import client.Client;
import constants.ItemConstants;
import lombok.Getter;
import lombok.Setter;
import util.packet.PacketWriter;

import java.util.Random;

public class ItemSlotLocker {

    private final @Getter ItemSlot item;
    private @Getter @Setter int commodityId, paybackRate, discountRate;
    private @Getter @Setter String buyCharacterName;

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
