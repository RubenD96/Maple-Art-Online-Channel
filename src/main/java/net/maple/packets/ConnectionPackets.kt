package net.maple.packets

import constants.ServerConstants
import net.maple.SendOpcode
import net.server.ChannelServer
import util.packet.Packet
import util.packet.PacketWriter
import java.net.InetAddress
import java.net.UnknownHostException

object ConnectionPackets {

    /**
     * Sends a hello packet.
     *
     * @param sendIv the IV used by the net.server for sending
     * @param recvIv the IV used by the net.server for receiving
     * @return Hello Packet
     */
    fun sendHello(sendIv: ByteArray, recvIv: ByteArray, testServer: Boolean): Packet {
        val pw = PacketWriter(14)

        pw.writeShort(14)
        pw.writeShort(ServerConstants.VERSION)
        pw.writeMapleString("1")
        pw.write(recvIv)
        pw.write(sendIv)
        pw.write(if (testServer) 5 else 8)

        return pw.createPacket()
    }

    /**
     * Sends a ping packet.
     *
     * @return The packet.
     */
    val ping: Packet
        get() {
            val pw = PacketWriter(2)

            pw.writeHeader(SendOpcode.PING)

            return pw.createPacket()
        }

    fun ChannelServer.getChangeChannelPacket(): Packet {
        val pw = PacketWriter(9)

        try {
            pw.writeHeader(SendOpcode.MIGRATE_COMMAND)
            pw.writeBool(true)
            pw.write(InetAddress.getByName(this.IP).address)
            pw.writeShort(this.port)
        } catch (uhe: UnknownHostException) {
            uhe.printStackTrace()
        }

        return pw.createPacket()
    }
}