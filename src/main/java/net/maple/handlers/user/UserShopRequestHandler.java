package net.maple.handlers.user;

import client.Character;
import client.Client;
import client.interaction.shop.NPCShop;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class UserShopRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        NPCShop shop = chr.getNpcShop();
        if (shop == null) return;

        byte request = reader.readByte();
        if (request == ShopRequest.CLOSE) {
            shop.close(c);
            return;
        }

        PacketWriter pw = new PacketWriter(16);
        pw.writeHeader(SendOpcode.SHOP_RESULT);
        switch (request) {
            case ShopRequest.BUY:
            case ShopRequest.SELL:
                short pos = reader.readShort();
                int itemId = reader.readInteger();
                short count = reader.readShort();

                pw.write((request == ShopRequest.BUY ?
                        shop.buy(chr, pos, itemId, count) :
                        shop.sell(chr, pos, itemId, count))
                        .getValue());
                break;
            case ShopRequest.RECHARGE:
                // todo
                break;
        }

        c.write(pw.createPacket());
    }

    private static final class ShopRequest {
        public static final byte BUY = 0x00;
        public static final byte SELL = 0x01;
        public static final byte RECHARGE = 0x02;
        public static final byte CLOSE = 0x03;
    }
}
