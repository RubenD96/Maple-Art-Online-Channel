package field.movement.fragment

import field.`object`.life.FieldLife
import field.movement.MovePathAttribute
import util.packet.PacketReader
import util.packet.PacketWriter
import java.awt.Point

class NormalMoveFragment(movePathAttribute: Byte, packetReader: PacketReader) : ActionMoveFragment(movePathAttribute, packetReader) {

    lateinit var position: Point
    lateinit var vposition: Point
    lateinit var offset: Point
    var foothold: Short = 0
    var fallStartFoothold: Short = 0

    override fun apply(life: FieldLife) {
        super.apply(life)
        life.position = position
        life.foothold = foothold
    }

    override fun decodeData(packet: PacketReader) {
        position = packet.readPoint()
        vposition = packet.readPoint()
        foothold = packet.readShort()

        if (movePathAttribute == MovePathAttribute.FALL_DOWN) {
            fallStartFoothold = packet.readShort()
        }

        offset = packet.readPoint()
        super.decodeData(packet)
    }

    override fun encodeData(packet: PacketWriter) {
        packet.writePosition(position)
        packet.writePosition(vposition)
        packet.writeShort(foothold)

        if (movePathAttribute == MovePathAttribute.FALL_DOWN) {
            packet.writeShort(fallStartFoothold)
        }

        packet.writePosition(offset)
        super.encodeData(packet)
    }
}