package field.movement.fragment

import util.packet.PacketReader
import util.packet.PacketWriter
import java.awt.Point

class StartFallDownMoveFragment(movePathAttribute: Byte, packetReader: PacketReader) : ActionMoveFragment(movePathAttribute, packetReader) {

    lateinit var vposition: Point
    var fallStartFoothold: Short = 0

    override fun decodeData(packet: PacketReader) {
        vposition = packet.readPoint()
        fallStartFoothold = packet.readShort()
        super.decodeData(packet)
    }

    override fun encodeData(packet: PacketWriter) {
        packet.writePosition(vposition)
        packet.writeShort(fallStartFoothold)
        super.encodeData(packet)
    }
}