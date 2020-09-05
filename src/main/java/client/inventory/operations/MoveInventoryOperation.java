package client.inventory.operations;

import client.inventory.ItemInventoryType;
import client.inventory.ModifyInventoryOperationType;
import util.packet.PacketWriter;

public class MoveInventoryOperation extends AbstractModifyInventoryOperation {

    final private short toSlot;

    public short getToSlot() {
        return toSlot;
    }

    public MoveInventoryOperation(ItemInventoryType inventory, short slot, short toSlot) {
        super(inventory, slot);
        this.toSlot = toSlot;
    }

    @Override
    protected ModifyInventoryOperationType getType() {
        return ModifyInventoryOperationType.MOVE;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeShort(toSlot);
    }
}
