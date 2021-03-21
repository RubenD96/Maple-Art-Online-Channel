package net.netty.central.handlers

import net.netty.central.CentralPacketHandler
import net.netty.central.CentralSendOpcode
import net.server.ChannelServer
import net.server.Server
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter

class OnConnectHandler : CentralPacketHandler {

    override fun handlePacket(reader: PacketReader, c: ChannelServer) {
        c.write(getChannelInfoPacket(c))
    }

    companion object {

        fun getChannelInfoPacket(c: ChannelServer): Packet {
            val pw = PacketWriter(8)

            pw.writeHeader(CentralSendOpcode.CHANNEL_INFO)
            pw.writeMapleString(c.IP)
            pw.writeInt(c.port)

            val restart = c.centralListener.restart
            pw.writeBool(restart)
            if (restart) {
                val clients = Server.clients.filter { it.value.port == c.port }
                pw.writeShort(clients.size)
                clients.values.forEach {
                    pw.writeInt(it.aid)
                }
            }

            return pw.createPacket()
        }
    }
}