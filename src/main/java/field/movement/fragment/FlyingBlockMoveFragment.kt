package field.movement.fragment

import field.`object`.life.FieldLife
import util.packet.PacketReader
import util.packet.PacketWriter
import java.awt.Point

class FlyingBlockMoveFragment(movePathAttribute: Byte, packetReader: PacketReader) : ActionMoveFragment(movePathAttribute, packetReader) {

    lateinit var position: Point
    lateinit var vposition: Point

    override fun apply(life: FieldLife) {
        super.apply(life)
        life.position = position
    }

    override fun decodeData(packet: PacketReader) {
        position = packet.readPoint()
        vposition = packet.readPoint()
        super.decodeData(packet)
    }

    override fun encodeData(packet: PacketWriter) {
        packet.writePosition(position)
        packet.writePosition(vposition)
        super.encodeData(packet)
    }
}