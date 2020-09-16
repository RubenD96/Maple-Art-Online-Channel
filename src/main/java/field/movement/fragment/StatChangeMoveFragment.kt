package field.movement.fragment

import field.obj.life.FieldLife
import util.packet.PacketReader
import util.packet.PacketWriter

class StatChangeMoveFragment(movePathAttribute: Byte, packetReader: PacketReader) : AbstractMovementFragment(movePathAttribute, packetReader) {

    var stat = false
    override fun apply(life: FieldLife) {}

    override fun decodeData(packet: PacketReader) {
        stat = packet.readBool()
    }

    override fun encodeData(packet: PacketWriter) {
        packet.writeBool(stat)
    }
}