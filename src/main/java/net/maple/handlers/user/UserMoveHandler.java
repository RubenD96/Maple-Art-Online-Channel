package net.maple.handlers.user;

import client.Character;
import client.Client;
import field.movement.MovePath;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class UserMoveHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();

        reader.readLong(); // probably timestamp
        reader.read();
        reader.readLong();
        reader.readInteger();
        reader.readInteger();
        reader.readInteger();

        MovePath path = chr.move(reader);

        if (chr.getField() != null) {
            chr.getField().broadcast(movePlayer(chr, path), chr);
        }
    }

    private static Packet movePlayer(Character chr, MovePath path) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.USER_MOVE);
        pw.writeInt(chr.getId());
        path.encode(pw);

        return pw.createPacket();
    }
}
