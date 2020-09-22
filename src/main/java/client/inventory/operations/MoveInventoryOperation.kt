package client.inventory.operations

import client.inventory.ItemInventoryType
import client.inventory.ModifyInventoryOperationType
import util.packet.PacketWriter

class MoveInventoryOperation(inventory: ItemInventoryType, slot: Short, val toSlot: Short) : AbstractModifyInventoryOperation(inventory, slot) {

    override val type = ModifyInventoryOperationType.MOVE

    override fun encodeData(pw: PacketWriter) {
        pw.writeShort(toSlot)
    }
}