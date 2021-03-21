package net.netty.central

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import util.packet.Packet

class CentralPacketEncoder : MessageToByteEncoder<Packet>() {

    override fun encode(chc: ChannelHandlerContext, packet: Packet, bb: ByteBuf) {
        bb.writeBytes(packet.data)
    }
}