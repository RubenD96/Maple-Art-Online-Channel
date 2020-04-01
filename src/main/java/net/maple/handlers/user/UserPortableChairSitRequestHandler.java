package net.maple.handlers.user;

import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import client.Character;
import client.Client;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class UserPortableChairSitRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        int chairId = reader.readInteger();

        chr.setPortableChair(chairId);

        chr.getField().broadcast(broadcastChairSit(chr.getId(), chairId), chr);
    }

    private static Packet broadcastChairSit(int cid, int chair) {
        PacketWriter pw = new PacketWriter(10);

        pw.writeHeader(SendOpcode.USER_SET_ACTIVE_PORTABLE_CHAIR);
        pw.writeInt(cid);
        pw.writeInt(chair);

        return pw.createPacket();
    }
}
