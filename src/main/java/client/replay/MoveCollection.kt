package client.replay

import client.Character
import util.Exportable
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter
import java.awt.Point

class MoveCollection(val chr: Character, val field: Int) : Exportable {

    companion object {
        // 2 (header) + 29 (see UserMoveHandler)
        const val HEADER_SIZE = 31
    }

    val movements = ArrayList<Movement>()

    override val location: String = "data/replays/$field.replay"

    override fun toByteStream(): Packet {
        val pw = PacketWriter(32)

        pw.writeInt(chr.gender)
        pw.writeInt(chr.skinColor)
        pw.writeInt(chr.face)
        pw.writeInt(chr.hair)
        pw.writeInt(chr.level)
        pw.writeMapleString(chr.name)
        pw.writeInt(chr.job)

        val firstTS = movements[0].timestamp

        pw.writeShort(movements.size)
        movements.forEach {
            pw.writeInt((it.timestamp - firstTS).toInt())
            pw.writeShort(it.data.size - HEADER_SIZE)
            pw.write(it.data, HEADER_SIZE)
        }

        return pw.createPacket()
    }

    class Movement(val timestamp: Long, val data: ByteArray)
}