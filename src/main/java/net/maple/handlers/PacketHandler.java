package net.maple.handlers;

import player.Client;
import util.packet.PacketReader;

public abstract class PacketHandler {

    public abstract void handlePacket(PacketReader reader, Client c);

    public boolean validateState(Client c) {
        return c.isLoggedIn();
    }
}
