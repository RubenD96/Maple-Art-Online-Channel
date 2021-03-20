package net.netty.central

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import net.netty.central.handlers.MigrateInfoHandler
import net.netty.central.handlers.OnConnectHandler
import net.server.ChannelServer
import util.HexTool
import util.logging.LogType
import util.logging.Logger
import util.packet.Packet
import util.packet.PacketReader
import java.util.*

class CentralServerHandler : ChannelInboundHandlerAdapter() {

    override fun channelRead(chc: ChannelHandlerContext, msg: Any) {
        val packet = msg as Packet

        val channel = chc.channel().attr(ChannelServer.CHANNEL_KEY).get()

        val reader = PacketReader().next(packet)
        CentralPacketProcessor.getHandler(reader.readShort())?.handlePacket(reader, channel)
            ?: Logger.log(LogType.CENTRAL, "Missing packet ${HexTool.toHex(packet.data)}", this)
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