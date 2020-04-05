package client.inventory.operations;

import client.inventory.ItemInventoryType;
import client.inventory.ModifyInventoryOperationType;
import util.packet.PacketWriter;

public class UpdateQuantityInventoryOperation extends AbstractModifyInventoryOperation {

    private final short quantity;

    public UpdateQuantityInventoryOperation(ItemInventoryType inventory, short slot, short quantity) {
        super(inventory, slot);
        this.quantity = quantity;
    }

    @Override
    protected ModifyInventoryOperationType getType() {
        return ModifyInventoryOperationType.UPDATE_QUANTITY;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeShort(quantity);
    }
}
