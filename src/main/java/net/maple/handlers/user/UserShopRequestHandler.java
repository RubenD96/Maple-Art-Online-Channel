package net.maple.handlers.user;

import client.Character;
import client.Client;
import client.shop.NPCShop;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

        ShopRequest request = ShopRequest.values()[reader.readByte()];
        if (request == ShopRequest.CLOSE) {
            shop.close(c);
            return;
        }

        PacketWriter pw = new PacketWriter(16);
        pw.writeHeader(SendOpcode.SHOP_RESULT);
        switch (request) {
            case BUY:
            case SELL:
                short pos = reader.readShort();
                int itemId = reader.readInteger();
                short count = reader.readShort();

                pw.write((request == ShopRequest.BUY ?
                        shop.buy(chr, pos, itemId, count) :
                        shop.sell(chr, pos, itemId, count))
                        .getValue());
                break;
            case RECHARGE:
                // todo
                break;
        }

        c.write(pw.createPacket());
    }

    @RequiredArgsConstructor
    private enum ShopRequest {
        BUY(0x00),
        SELL(0x01),
        RECHARGE(0x02),
        CLOSE(0x03);

        private final @Getter int value;
    }
}
