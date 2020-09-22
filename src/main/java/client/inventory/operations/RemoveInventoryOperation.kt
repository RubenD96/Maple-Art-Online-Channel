package client.inventory.operations

import client.inventory.ItemInventoryType
import client.inventory.ModifyInventoryOperationType
import util.packet.PacketWriter

class RemoveInventoryOperation(inventory: ItemInventoryType, slot: Short) : AbstractModifyInventoryOperation(inventory, slot) {

    override val type = ModifyInventoryOperationType.REMOVE

    override fun encodeData(pw: PacketWriter) {
        // keep empty
    }
}