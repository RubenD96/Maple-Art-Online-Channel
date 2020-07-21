package net.maple.handlers.user;

import client.Character;
import client.Client;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import net.server.ChannelServer;
import net.server.Server;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class UserTransferChannelRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();

        byte channelId = reader.readByte();
        // todo field limit check

        try {
            ChannelServer channel = Server.getInstance().getChannels().get(channelId);
            if (channel != null) {
                if (!c.getWorldChannel().equals(channel)) {
                    c.acquireMigrateState();
                    try {
                        //c.getCharacter().save();
                        c.changeChannel(channel);
                    } finally {
                        c.releaseMigrateState();
                    }
                    return;
                }
                c.write(fail());
                //c.close(this, "CC to same channel");
            } else {
                System.err.println("[UserTransferChannelRequestHandler] Channel is null (" + chr + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        c.write(fail());
    }

    private static Packet fail() {
        PacketWriter pw = new PacketWriter(3);

        pw.writeHeader(SendOpcode.TRANSFER_CHANNEL_REQ_IGNORED);
        pw.write(0x01);

        return pw.createPacket();
    }
}
