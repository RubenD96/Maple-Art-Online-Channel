package field.movement.fragment

import field.obj.life.FieldLife
import util.packet.PacketReader
import util.packet.PacketWriter

interface MoveFragment {
    fun apply(life: FieldLife)
    fun decode(packet: PacketReader)
    fun encode(packet: PacketWriter)
}