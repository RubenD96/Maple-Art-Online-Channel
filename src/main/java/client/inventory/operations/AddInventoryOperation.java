package client.inventory.operations;

import client.inventory.ItemInventoryType;
import client.inventory.ModifyInventoryOperationType;
import client.inventory.slots.ItemSlot;
import net.maple.packets.ItemPackets;
import util.packet.PacketWriter;

public class AddInventoryOperation extends AbstractModifyInventoryOperation {

    private final ItemSlot item;

    public AddInventoryOperation(ItemInventoryType inventory, short slot, ItemSlot item) {
        super(inventory, slot);
        this.item = item;
    }

    @Override
    protected ModifyInventoryOperationType getType() {
        return ModifyInventoryOperationType.ADD;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        ItemPackets.encode(item, pw);
    }
}
