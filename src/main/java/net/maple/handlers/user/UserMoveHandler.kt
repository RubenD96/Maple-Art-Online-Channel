package net.maple.handlers.user

import client.Character
import client.Client
import field.movement.MovePath
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter

class UserMoveHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        reader.readLong() // probably timestamp
        reader.read()
        reader.readLong()
        reader.readInteger()
        reader.readInteger()
        reader.readInteger()

        val path = chr.move(reader)

        chr.field.broadcast(movePlayer(chr, path), chr)
    }

    companion object {
        private fun movePlayer(chr: Character, path: MovePath): Packet {
            val pw = PacketWriter(32)

            pw.writeHeader(SendOpcode.USER_MOVE)
            pw.writeInt(chr.id)
            path.encode(pw)

            return pw.createPacket()
        }
    }
}