package net.netty.central

import util.packet.Packet
import util.packet.PacketWriter

object CentralPackets {

    fun getAddOnlinePlayerPacket(port: Int, aid: Int): Packet {
        val pw = PacketWriter(10)

        pw.writeHeader(CentralSendOpcode.ADD_ONLINE_PLAYER)
        pw.writeInt(port)
        pw.writeInt(aid)

        return pw.createPacket()
    }

    fun getRemoveOnlinePlayerPacket(port: Int, aid: Int): Packet {
        val pw = PacketWriter(10)

        pw.writeHeader(CentralSendOpcode.REMOVE_ONLINE_PLAYER)
        pw.writeInt(port)
        pw.writeInt(aid)

        return pw.createPacket()
    }

    fun getPingPacket(): Packet {
        val pw = PacketWriter(2)

        pw.writeHeader(CentralSendOpcode.PING)

        return pw.createPacket()
    }
}