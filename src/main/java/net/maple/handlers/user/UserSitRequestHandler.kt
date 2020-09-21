package net.maple.handlers.user

import client.Character
import client.Client
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter

class UserSitRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character
        val chairId = reader.readShort()

        c.write(sit(chr, chairId))

        if (chr.portableChair == null) {
            chr.field.broadcast(broadcastSit(chr.id), chr)
        }
    }

    companion object {
        private fun sit(chr: Character, id: Short): Packet {
            val pw = PacketWriter(3)

            pw.writeHeader(SendOpcode.USER_SIT_RESULT)
            if (id < 0) {
                chr.portableChair = null
                pw.write(0)
            } else {
                pw.write(1)
                pw.writeShort(id)
            }

            return pw.createPacket()
        }

        private fun broadcastSit(cid: Int): Packet {
            val pw = PacketWriter(10)

            pw.writeHeader(SendOpcode.USER_SET_ACTIVE_PORTABLE_CHAIR)
            pw.writeInt(cid)
            pw.writeInt(0)

            return pw.createPacket()
        }
    }
}