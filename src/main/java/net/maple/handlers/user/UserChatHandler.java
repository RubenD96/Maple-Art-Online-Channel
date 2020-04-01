package net.maple.handlers.user;

import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import player.Character;
import player.Client;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class UserChatHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();

        reader.readInteger(); // ?

        String msg = reader.readMapleString();
        boolean textBox = !reader.readBool();

        System.out.println("Message from " + chr + " = " + msg);
        chr.getField().broadcast(sendMessage(chr, msg, textBox), null);
    }

    private static Packet sendMessage(Character chr, String msg, boolean textBox) {
        PacketWriter pw = new PacketWriter(16);

        pw.writeHeader(SendOpcode.USER_CHAT);
        pw.writeInt(chr.getId());
        pw.writeBool(/*chr.isGM()*/ false);
        pw.writeMapleString(msg);
        pw.writeBool(!textBox);

        return pw.createPacket();
    }
}
