package net.maple.handlers.misc;

import client.Client;
import net.maple.handlers.PacketHandler;
import util.packet.PacketReader;

public class AdminVerificationHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        if (!c.isAdmin()) {
            c.close(this, "Admin packet from non-admin user (" + c.getCharacter().getName() + ")");
        }
    }
}
