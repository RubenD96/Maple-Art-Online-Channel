package client.inventory.operations

import client.inventory.ItemInventoryType
import client.inventory.ModifyInventoryOperationType
import util.packet.PacketWriter

abstract class AbstractModifyInventoryOperation(val inventory: ItemInventoryType, val slot: Short) {

    protected abstract val type: ModifyInventoryOperationType

    fun encode(pw: PacketWriter) {
        pw.writeByte(type.type.toByte())
        pw.writeByte(inventory.type.toByte())
        pw.writeShort(slot)
        encodeData(pw)
    }

    protected abstract fun encodeData(pw: PacketWriter)
}