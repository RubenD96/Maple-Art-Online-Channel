package client.inventory.operations;

import client.inventory.ItemInventoryType;
import client.inventory.ModifyInventoryOperationType;
import util.packet.PacketWriter;

public class RemoveInventoryOperation extends AbstractModifyInventoryOperation {

    public RemoveInventoryOperation(ItemInventoryType inventory, short slot) {
        super(inventory, slot);
    }

    @Override
    protected ModifyInventoryOperationType getType() {
        return ModifyInventoryOperationType.REMOVE;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        // keep empty
    }
}
