package net.maple.packets;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.maple.SendOpcode;
import util.packet.Packet;
import util.packet.PacketWriter;

public class ConversationPackets {

    @RequiredArgsConstructor
    private enum ConversationType {
        SAY(0x00);

        @Getter @NonNull private final int value;
    }

    private static PacketWriter getMessagePacket(int npc, ConversationType type, int speaker, String text) {
        PacketWriter pw = new PacketWriter(16);

        pw.writeHeader(SendOpcode.SCRIPT_MESSAGE);
        pw.write(0); // SpeakerTypeID
        pw.writeInt(npc);
        pw.write(type.getValue());
        pw.write(speaker);
        pw.writeMapleString(text);

        return pw;
    }

    private static Packet getSayMessagePacket(int npc, int speaker, String text, boolean prev, boolean next) {
        PacketWriter pw = getMessagePacket(npc, ConversationType.SAY, speaker, text);

        pw.writeBool(prev);
        pw.writeBool(next);

        return pw.createPacket();
    }

    public static Packet getOkMessagePacket(int npc, int speaker, String text) {
        return getSayMessagePacket(npc, speaker, text, false, false);
    }

    public static Packet getPrevMessagePacket(int npc, int speaker, String text) {
        return getSayMessagePacket(npc, speaker, text, true, false);
    }

    public static Packet getNextMessagePacket(int npc, int speaker, String text) {
        return getSayMessagePacket(npc, speaker, text, false, true);
    }

    public static Packet getNextPrevMessagePacket(int npc, int speaker, String text) {
        return getSayMessagePacket(npc, speaker, text, true, true);
    }
}
