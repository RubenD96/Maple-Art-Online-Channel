package net.netty.central

import net.server.ChannelServer
import util.packet.PacketReader

interface CentralPacketHandler {

    fun handlePacket(reader: PacketReader, c: ChannelServer)
}