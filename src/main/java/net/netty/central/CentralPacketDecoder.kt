package net.netty.central

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import net.server.ChannelServer.Companion.CHANNEL_KEY
import util.packet.Packet

class CentralPacketDecoder : ByteToMessageDecoder() {

    override fun decode(chc: ChannelHandlerContext, byteBuf: ByteBuf, out: MutableList<Any>) {
        val dec = ByteArray(byteBuf.readableBytes())
        byteBuf.readBytes(dec)
        out.add(Packet(dec))
    }
}