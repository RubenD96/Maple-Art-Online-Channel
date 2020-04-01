package net.maple.handlers.user;

import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import client.Character;
import client.Client;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class UserSitRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        short chairId = reader.readShort();

        c.write(sit(chr, chairId));

        if (chr.getPortableChair() == null) {
            chr.getField().broadcast(broadcastSit(chr.getId()), chr);
        }
    }

    private static Packet sit(Character chr, short id) {
        PacketWriter pw = new PacketWriter(3);

        pw.writeHeader(SendOpcode.USER_SIT_RESULT);
        if (id < 0) {
            chr.setPortableChair(null);
            pw.write(0);
        } else {
            pw.write(1);
            pw.writeShort(id);
        }

        return pw.createPacket();
    }

    private static Packet broadcastSit(int cid) {
        PacketWriter pw = new PacketWriter(10);

        pw.writeHeader(SendOpcode.USER_SET_ACTIVE_PORTABLE_CHAIR);
        pw.writeInt(cid);
        pw.writeInt(0);

        return pw.createPacket();
    }
}
