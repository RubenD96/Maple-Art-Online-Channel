package net.maple.handlers;

import client.Client;
import util.packet.PacketReader;

public class DoNothingHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        // ...
    }

    @Override
    public boolean validateState(Client c) {
        return true;
    }
}
