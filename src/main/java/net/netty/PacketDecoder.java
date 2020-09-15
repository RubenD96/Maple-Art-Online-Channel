package net.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import util.crypto.MapleAESOFB;
import util.crypto.ShandaCrypto;
import util.packet.Packet;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf in, List<Object> out) {
        NettyClient c = chc.channel().attr(NettyClient.Companion.getCLIENT_KEY()).get();
        MapleAESOFB mCr = chc.channel().attr(NettyClient.Companion.getCRYPTO_KEY()).get();

        if (c != null) {
            byte[] iv = c.getRecvIV();
            if (c.getStoredLength() == -1) {
                if (in.readableBytes() >= 4) {
                    int h = in.readInt();
                    if (!MapleAESOFB.checkPacket(h, iv)) {
                        c.close();
                        return;
                    }
                    c.setStoredLength(MapleAESOFB.getLength(h));
                } else {
                    return;
                }
            }
            if (in.readableBytes() >= c.getStoredLength()) {
                byte[] dec = new byte[c.getStoredLength()];
                in.readBytes(dec);
                c.setStoredLength(-1);

                dec = mCr.crypt(dec, iv);
                c.setRecvIV(MapleAESOFB.getNewIv(iv));

                ShandaCrypto.decrypt(dec);

                out.add(new Packet(dec));
            }
        }
    }
}