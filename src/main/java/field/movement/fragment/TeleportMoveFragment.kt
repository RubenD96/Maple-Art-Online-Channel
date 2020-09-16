package field.movement.fragment

import field.obj.life.FieldLife
import util.packet.PacketReader
import util.packet.PacketWriter
import java.awt.Point

class TeleportMoveFragment(movePathAttribute: Byte, packetReader: PacketReader) : ActionMoveFragment(movePathAttribute, packetReader) {

    lateinit var position: Point
    var foothold: Short = 0

    override fun apply(life: FieldLife) {
        super.apply(life)
        life.position = position
        life.foothold = foothold
    }

    override fun decodeData(packet: PacketReader) {
        position = packet.readPoint()
        foothold = packet.readShort()
        super.decodeData(packet)
    }

    override fun encodeData(packet: PacketWriter) {
        packet.writePosition(position)
        packet.writeShort(foothold)
        super.encodeData(packet)
    }
}