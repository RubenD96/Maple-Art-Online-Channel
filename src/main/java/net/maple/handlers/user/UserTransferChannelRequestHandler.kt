package net.maple.handlers.user

import client.Client
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import net.server.Server.channels
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter

class UserTransferChannelRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character
        val channelId = reader.readByte()
        // todo field limit check
        try {
            val id = channelId.toInt();
            val channel = channels[id]
            if (id < 0 || id >= channels.size) {
                if (c.worldChannel != channel) {
                    c.acquireMigrateState()
                    try {
                        //c.getCharacter().save();
                        c.changeChannel(channel)
                    } finally {
                        c.releaseMigrateState()
                    }
                    return
                }
                c.write(fail())
                //c.close(this, "CC to same channel");
            } else {
                System.err.println("[UserTransferChannelRequestHandler] Channel is null ($chr)")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        c.write(fail())
    }

    companion object {
        private fun fail(): Packet {
            val pw = PacketWriter(3)

            pw.writeHeader(SendOpcode.TRANSFER_CHANNEL_REQ_IGNORED)
            pw.write(0x01)

            return pw.createPacket()
        }
    }
}