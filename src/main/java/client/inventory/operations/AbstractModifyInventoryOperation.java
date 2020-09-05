package client.inventory.operations;

import client.inventory.ItemInventoryType;
import client.inventory.ModifyInventoryOperationType;
import util.packet.PacketWriter;

public abstract class AbstractModifyInventoryOperation {

    protected abstract ModifyInventoryOperationType getType();

    private final ItemInventoryType inventory;
    private final short slot;

    public ItemInventoryType getInventory() {
        return inventory;
    }

    public short getSlot() {
        return slot;
    }

    public AbstractModifyInventoryOperation(ItemInventoryType inventory, short slot) {
        this.inventory = inventory;
        this.slot = slot;
    }

    public void encode(PacketWriter pw) {
        pw.writeByte((byte) getType().getType());
        pw.writeByte((byte) inventory.getType());
        pw.writeShort(slot);

        encodeData(pw);
    }

    protected abstract void encodeData(PacketWriter pw);
}
