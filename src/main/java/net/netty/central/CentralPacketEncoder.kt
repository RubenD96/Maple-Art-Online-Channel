package net.netty.central

import constants.ServerConstants
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import util.HexTool
import util.packet.Packet

class CentralPacketEncoder : MessageToByteEncoder<Packet>() {

    override fun encode(chc: ChannelHandlerContext, packet: Packet, bb: ByteBuf) {
        if (ServerConstants.DEBUG) {
            println("[CentralPacketEncoder] trying to send packet: " + HexTool.toHex(packet.data))
        }
        bb.writeBytes(packet.data)
    }
}