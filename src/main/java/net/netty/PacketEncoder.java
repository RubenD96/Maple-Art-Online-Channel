package net.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import util.crypto.MapleAESOFB;
import util.crypto.ShandaCrypto;
import util.packet.Packet;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext chc, Packet in, ByteBuf bb) {
        byte[] data = in.getData();

        NettyClient c = chc.channel().attr(NettyClient.Companion.getCLIENT_KEY()).get();
        MapleAESOFB mCr = chc.channel().attr(NettyClient.Companion.getCRYPTO_KEY()).get();

        if (c != null) {
            byte[] iv = c.getSendIV();
            byte[] head = MapleAESOFB.getHeader(data.length, iv);

            ShandaCrypto.encrypt(data);

            c.acquireEncoderState();
            try {
                mCr.crypt(data, iv);
                c.setSendIV(MapleAESOFB.getNewIv(iv));
            } finally {
                c.releaseEncodeState();
            }

            bb.writeBytes(head);
        }
        bb.writeBytes(data);
    }
}
