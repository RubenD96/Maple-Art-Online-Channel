package net.maple.handlers.misc;

import client.Client;
import net.maple.handlers.PacketHandler;
import net.maple.packets.CashShopPackets;
import util.packet.PacketReader;

public class CashShopQueryCashRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        CashShopPackets.sendCashData(c);
    }
}
