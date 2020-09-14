package field.movement.fragment

import util.packet.PacketReader
import util.packet.PacketWriter
import java.awt.Point

class JumpMoveFragment(movePathAttribute: Byte, packetReader: PacketReader) : ActionMoveFragment(movePathAttribute, packetReader) {

    lateinit var vposition: Point

    override fun decodeData(packet: PacketReader) {
        vposition = packet.readPoint()
        super.decodeData(packet)
    }

    override fun encodeData(packet: PacketWriter) {
        packet.writePosition(vposition)
        super.encodeData(packet)
    }
}