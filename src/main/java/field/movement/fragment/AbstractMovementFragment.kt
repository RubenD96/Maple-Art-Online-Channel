package field.movement.fragment

import field.obj.life.FieldLife
import util.packet.PacketReader
import util.packet.PacketWriter

abstract class AbstractMovementFragment(protected val movePathAttribute: Byte, packetReader: PacketReader) : MoveFragment {

    abstract override fun apply(life: FieldLife)
    abstract fun decodeData(packet: PacketReader)
    abstract fun encodeData(packet: PacketWriter)

    final override fun decode(packet: PacketReader) {
        decodeData(packet)
    }

    override fun encode(packet: PacketWriter) {
        packet.write(movePathAttribute.toInt())
        encodeData(packet)
    }

    init {
        decode(packetReader)
    }
}