package net.maple.handlers.user

import client.Client
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter

class UserPortableChairSitRequestHandler : PacketHandler() {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character
        val chairId = reader.readInteger()

        chr.portableChair = chairId

        chr.enableActions()
        chr.field.broadcast(broadcastChairSit(chr.id, chairId), chr)
    }

    companion object {
        private fun broadcastChairSit(cid: Int, chair: Int): Packet {
            val pw = PacketWriter(10)

            pw.writeHeader(SendOpcode.USER_SET_ACTIVE_PORTABLE_CHAIR)
            pw.writeInt(cid)
            pw.writeInt(chair)

            return pw.createPacket()
        }
    }
}