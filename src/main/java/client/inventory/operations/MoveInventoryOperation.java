package client.inventory.operations;

import client.inventory.ItemInventoryType;
import client.inventory.ModifyInventoryOperationType;
import lombok.Getter;
import util.packet.PacketWriter;

public class MoveInventoryOperation extends AbstractModifyInventoryOperation {

    @Getter private short toSlot;

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
