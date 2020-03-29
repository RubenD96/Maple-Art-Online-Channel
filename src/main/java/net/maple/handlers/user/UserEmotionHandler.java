package net.maple.handlers.user;

import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import player.Character;
import player.Client;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class UserEmotionHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        int emotion = reader.readInteger();
        int duration = reader.readInteger();
        boolean item = reader.readBool();

        chr.getField().broadcast(sendEmotion(chr, emotion, duration, item), chr);
    }

    private static Packet sendEmotion(Character chr, int emotion, int duration, boolean item) {
        PacketWriter pw = new PacketWriter(15);

        pw.writeHeader(SendOpcode.USER_EMOTION);
        pw.writeInt(chr.getId());
        pw.writeInt(emotion);
        pw.writeInt(duration);
        pw.writeBool(item);

        return pw.createPacket();
    }
}
