package net.maple.handlers.net;

import client.Client;
import net.maple.handlers.PacketHandler;
import util.packet.PacketReader;

public class PongHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        c.setLastPong(System.currentTimeMillis());
    }

    @Override
    public boolean validateState(Client c) {
        return true;
    }
}
