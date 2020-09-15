package net.maple.handlers.net;

import client.Client;
import net.maple.handlers.PacketHandler;
import util.HexTool;
import util.packet.PacketReader;

public class ClientDumpLogHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        System.out.println("[ClientDumpLogHandler] " + HexTool.INSTANCE.toHex(reader.getData()));
    }
}