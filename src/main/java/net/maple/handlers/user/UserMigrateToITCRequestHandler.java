package net.maple.handlers.user;

import client.Client;
import net.maple.handlers.PacketHandler;
import util.packet.PacketReader;

public class UserMigrateToITCRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        c.getCharacter().enableActions();
    }
}
