package net.netty.central.handlers

import net.netty.central.CentralPacketHandler
import net.server.ChannelServer
import net.server.MigrateInfo
import net.server.Server
import util.packet.PacketReader

class MigrateInfoHandler : CentralPacketHandler {

    override fun handlePacket(reader: PacketReader, c: ChannelServer) {
        val id = reader.readInteger()
        val port = reader.readInteger()
        val ip = reader.readMapleString()

        val mi = MigrateInfo(id, port, ip)
        mi.channelId = Server.channels.first { it.port == port }.channelId

        Server.clients[id] = mi
    }
}