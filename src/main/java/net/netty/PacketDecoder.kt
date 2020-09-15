package net.netty

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import net.netty.NettyClient.Companion.CLIENT_KEY
import net.netty.NettyClient.Companion.CRYPTO_KEY
import util.crypto.MapleAESOFB
import util.crypto.ShandaCrypto
import util.packet.Packet

class PacketDecoder : ByteToMessageDecoder() {

    override fun decode(chc: ChannelHandlerContext, byteBuf: ByteBuf, out: MutableList<Any>) {
        val c = chc.channel().attr(CLIENT_KEY).get()
        val mCr = chc.channel().attr(CRYPTO_KEY).get()

        if (c != null) {
            val iv = c.recvIV
            if (c.storedLength == -1) {
                if (byteBuf.readableBytes() >= 4) {
                    val h = byteBuf.readInt()
                    if (!MapleAESOFB.checkPacket(h, iv)) {
                        c.close()
                        return
                    }
                    c.storedLength = MapleAESOFB.getLength(h)
                } else {
                    return
                }
            }
            if (byteBuf.readableBytes() >= c.storedLength) {
                var dec = ByteArray(c.storedLength)
                byteBuf.readBytes(dec)
                c.storedLength = -1

                dec = mCr.crypt(dec, iv)
                c.recvIV = MapleAESOFB.getNewIv(iv)

                ShandaCrypto.decrypt(dec)

                out.add(Packet(dec))
            }
        }
    }
}