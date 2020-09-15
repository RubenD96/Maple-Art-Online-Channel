package net.netty

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import net.netty.NettyClient.Companion.CLIENT_KEY
import net.netty.NettyClient.Companion.CRYPTO_KEY
import util.crypto.MapleAESOFB
import util.crypto.ShandaCrypto
import util.packet.Packet

class PacketEncoder : MessageToByteEncoder<Packet>() {

    override fun encode(chc: ChannelHandlerContext, packet: Packet, bb: ByteBuf) {
        val data = packet.data

        val c = chc.channel().attr(CLIENT_KEY).get()
        val mCr = chc.channel().attr(CRYPTO_KEY).get()

        if (c != null) {
            val iv = c.sendIV
            val head = MapleAESOFB.getHeader(data.size, iv)

            ShandaCrypto.encrypt(data)

            c.acquireEncoderState()
            try {
                mCr.crypt(data, iv)
                c.sendIV = MapleAESOFB.getNewIv(iv)
            } finally {
                c.releaseEncodeState()
            }

            bb.writeBytes(head)
        }
        bb.writeBytes(data)
    }
}