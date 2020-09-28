package net.netty

import client.Client
import constants.ServerConstants
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import net.maple.PacketProcessor
import net.maple.RecvOpcode
import net.maple.packets.ConnectionPackets
import net.netty.NettyClient.Companion.CLIENT_KEY
import net.netty.NettyClient.Companion.CRYPTO_KEY
import util.crypto.MapleAESOFB
import util.packet.Packet
import java.util.*

class ServerHandler : ChannelInboundHandlerAdapter() {

    private var processor = PacketProcessor
    private var ignoreOps = intArrayOf(
            RecvOpcode.PONG.value,
            RecvOpcode.UPDATE_SCREEN_SETTING.value,
            RecvOpcode.USER_MOVE.value,
            RecvOpcode.NPC_MOVE.value,
            RecvOpcode.USER_CHANGE_STAT_REQUEST.value,
            RecvOpcode.MOB_MOVE.value,
            RecvOpcode.MOB_APPLY_CTRL.value,
            RecvOpcode.ADMIN.value
    )

    override fun channelActive(chc: ChannelHandlerContext) {
        val channel = chc.channel()
        val ivSend = byteArrayOf(82, 48, 120, (Math.random() * 255).toInt().toByte())
        val ivRecv = byteArrayOf(70, 114, 122, (Math.random() * 255).toInt().toByte())

        val client = Client(channel, ivSend, ivRecv)

        client.write(ConnectionPackets.sendHello(ivSend, ivRecv, false))

        channel.attr(CLIENT_KEY).set(client)
        channel.attr(CRYPTO_KEY).set(MapleAESOFB())

        System.out.printf("Opened session with %s%n", client.ip)

        client.startPing()
    }

    override fun channelRead(chc: ChannelHandlerContext, msg: Any) {
        val packet = msg as Packet
        val channel = chc.channel()

        val client = channel.attr(CLIENT_KEY).get() as Client
        val packetReader = client.reader.next(packet)

        val opCode = packetReader.readShort()
        val packetHandler = processor.getHandler(opCode)

        val hex = Integer.toHexString(opCode.toInt())
        if (ServerConstants.LOG && !ignoreOps.contains(opCode.toInt())/* Arrays.stream(ignoreOps).noneMatch { it == opCode.toInt() }*/) {
            if (packetHandler == null) {
                println("[RECEIVED] packet " + opCode + " (" + (if (hex.length == 1) "0x0" else "0x") + hex.toUpperCase() + ")")
            } else {
                val className = packetHandler.javaClass.name
                if (!className.contains("DoNothingHandler")) {
                    println("[RECEIVED] $className")
                }
            }
            //System.out.printf("data: %s.%n", packet.toString());
        }

        packetHandler?.let {
            if (it.validateState(client)) {
                it.handlePacket(packetReader, client)
            } else {
                System.out.printf("Client failed to validate state for packet %s.%n", opCode)
                channel.close()
            }
        } ?: System.out.printf("Received completely unhandled packet %s.%n", packet.toString())
    }

    override fun channelInactive(chc: ChannelHandlerContext) {
        val ch = chc.channel()
        val c = ch.attr(CLIENT_KEY).get() as Client

        c.disconnect() // todo npe on character? (lateinit, dc before init)
        //c.softDisconnect(c.isLoggedIn()); // handle this is we don't soft disconnect through handler
        c.cancelPingTask()

        // remove after debug stage
        System.out.printf("[Debug] Closed session with %s.%n", c.ip)
    }

    override fun exceptionCaught(chc: ChannelHandlerContext, cause: Throwable) {
        // Close the connection when an exception is raised.
        val ch = chc.channel()
        val c = ch.attr(CLIENT_KEY).get() as Client
        //c.disconnect();
        //System.out.println(cause);
        cause.printStackTrace()
        //chc.close();
    }
}