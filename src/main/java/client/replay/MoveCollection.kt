package client.replay

import client.Character
import net.maple.packets.CharacterPackets.encodeVisualEquips
import util.Exportable
import util.packet.Packet
import util.packet.PacketWriter

class MoveCollection(val chr: Character, val field: Int) : Exportable {

    companion object {
        // 2 (header) + 29 (see UserMoveHandler)
        const val HEADER_SIZE = 31
    }

    val movements = ArrayList<Movement>()
    val emotes = ArrayList<Emote>()

    override val fileName: String = "$field.replay"
    override val location: String = "data/replays/"
    override val backups: String = "data/replays/backups/"

    override fun toByteStream(): Packet {
        val pw = PacketWriter(32)

        pw.writeInt(chr.gender)
        pw.writeInt(chr.skinColor)
        pw.writeInt(chr.face)
        pw.writeInt(chr.hair)
        pw.writeInt(chr.level)
        pw.writeMapleString(chr.name)
        pw.writeInt(chr.job)

        chr.encodeVisualEquips(pw)

        val firstTS = movements[0].timestamp

        pw.writeShort(movements.size)
        movements.forEach {
            pw.writeInt((it.timestamp - firstTS).toInt())
            pw.writeShort(it.data.size - HEADER_SIZE)
            pw.write(it.data, HEADER_SIZE)
        }

        pw.writeShort(emotes.size)
        emotes.forEach {
            pw.writeInt((it.timestamp - firstTS).toInt())
            pw.writeInt(it.emote)
        }

        return pw.createPacket()
    }

    class Movement(val timestamp: Long, val data: ByteArray)
    class Emote(val timestamp: Long, val emote: Int)
}