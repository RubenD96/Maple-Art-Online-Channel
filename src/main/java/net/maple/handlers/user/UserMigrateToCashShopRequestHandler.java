package net.maple.handlers.user;

import client.Character;
import client.Client;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import net.maple.packets.CashShopPackets;
import net.server.Server;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class UserMigrateToCashShopRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        c.acquireMigrateState();
        try {
            if (!chr.isInCashShop()) {
                chr.save();
                chr.setInCashShop(true);
                Server.INSTANCE.getClients().get(c.getAccId()).setCashShop(true);
                CashShopPackets.sendSetCashShop(c);
                chr.getField().leave(chr);
                chr.setField(null);
            } else {
                c.write(fail());
            }
        } finally {
            c.releaseMigrateState();
        }
    }

    private static Packet fail() {
        PacketWriter pw = new PacketWriter(3);

        pw.writeHeader(SendOpcode.TRANSFER_CHANNEL_REQ_IGNORED);
        pw.write(0x02);

        return pw.createPacket();
    }
}
