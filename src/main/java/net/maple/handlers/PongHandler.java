package net.maple.handlers;

import client.Client;
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
