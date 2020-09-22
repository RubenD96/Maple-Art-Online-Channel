package client.inventory.operations

import client.inventory.ItemInventoryType
import client.inventory.ModifyInventoryOperationType
import util.packet.PacketWriter

class UpdateQuantityInventoryOperation(inventory: ItemInventoryType, slot: Short, private val quantity: Short) : AbstractModifyInventoryOperation(inventory, slot) {

    override val type = ModifyInventoryOperationType.UPDATE_QUANTITY

    override fun encodeData(pw: PacketWriter) {
        pw.writeShort(quantity)
    }
}