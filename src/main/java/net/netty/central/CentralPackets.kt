package net.netty.central

import util.packet.Packet
import util.packet.PacketWriter

object CentralPackets {

    fun getAddOnlinePlayerPacket(channel: Int, aid: Int): Packet {
        val pw = PacketWriter(10)

        pw.writeHeader(CentralSendOpcode.ADD_ONLINE_PLAYER)
        pw.writeInt(channel)
        pw.writeInt(aid)

        return pw.createPacket()
    }

    fun getRemoveOnlinePlayerPacket(channel: Int, aid: Int): Packet {
        val pw = PacketWriter(10)

        pw.writeHeader(CentralSendOpcode.REMOVE_ONLINE_PLAYER)
        pw.writeInt(channel)
        pw.writeInt(aid)

        return pw.createPacket()
    }
}