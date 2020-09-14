package field.movement.fragment

import field.`object`.life.FieldLife
import util.packet.PacketReader
import util.packet.PacketWriter

open class ActionMoveFragment(movePathAttribute: Byte, packetReader: PacketReader) : AbstractMovementFragment(movePathAttribute, packetReader) {

    var moveAction: Byte = 0
    var elapse: Short = 0

    override fun apply(life: FieldLife) {
        life.moveAction = moveAction
    }

    override fun decodeData(packet: PacketReader) {
        moveAction = packet.readByte()
        elapse = packet.readShort()
    }

    override fun encodeData(packet: PacketWriter) {
        packet.write(moveAction.toInt())
        packet.writeShort(elapse)
    }
}