package net.netty.central

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import net.netty.central.handlers.MigrateInfoHandler
import net.netty.central.handlers.OnConnectHandler
import net.server.CentralListener
import net.server.ChannelServer
import util.HexTool
import util.logging.LogType
import util.logging.Logger
import util.packet.Packet
import util.packet.PacketReader
import java.net.SocketException
import java.util.*

class CentralServerHandler : ChannelInboundHandlerAdapter() {

    override fun channelRead(chc: ChannelHandlerContext, msg: Any) {
        val packet = msg as Packet

        val channel = chc.channel().attr(ChannelServer.CHANNEL_KEY).get()

        val reader = PacketReader().next(packet)
        CentralPacketProcessor.getHandler(reader.readShort())?.handlePacket(reader, channel)
            ?: Logger.log(LogType.CENTRAL, "Missing packet ${HexTool.toHex(packet.data)}", this)
    }

    override fun exceptionCaught(chc: ChannelHandlerContext, cause: Throwable) {
        if (cause is SocketException) {
            val channel = chc.channel().attr(ChannelServer.CHANNEL_KEY).get()
            println("Attempting to reconnect to login server for channel on port ${channel.port}")

            channel.centralListener = CentralListener(channel, true)
            channel.centralListener.start()
        } else {
            cause.printStackTrace()
        }
    }

    private object CentralPacketProcessor {

        private val handlers: MutableMap<CentralRecvOpcode, CentralPacketHandler> =
            EnumMap(CentralRecvOpcode::class.java)

        fun getHandler(packetId: Short): CentralPacketHandler? {
            return handlers[Arrays.stream(
                CentralRecvOpcode.values()
            ).filter { it.value == packetId.toInt() }
                .findFirst().orElse(null)]
        }

        init {
            handlers[CentralRecvOpcode.ON_CONNECT] = OnConnectHandler()
            handlers[CentralRecvOpcode.MIGRATE_INFO] = MigrateInfoHandler()
        }
    }
}