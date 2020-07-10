package net.maple.packets;

import net.maple.SendOpcode;
import scripting.npc.ConversationType;
import util.packet.Packet;
import util.packet.PacketWriter;

public class ConversationPackets {

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

    public static Packet getYesNoMessagePacket(int npc, int speaker, String text) {
        return getMessagePacket(npc, ConversationType.ASK_YES_NO, speaker, text).createPacket();
    }

    public static Packet getTextMessagePacket(int npc, int speaker, String text, String def, int min, int max) {
        PacketWriter pw = getMessagePacket(npc, ConversationType.ASK_TEXT, speaker, text);
        pw.writeMapleString(def);
        pw.writeShort(min);
        pw.writeShort(max);

        return pw.createPacket();
    }

    public static Packet getNumberMessagePacket(int npc, int speaker, String text, int def, int min, int max) {
        PacketWriter pw = getMessagePacket(npc, ConversationType.ASK_NUMBER, speaker, text);
        pw.writeInt(def);
        pw.writeInt(min);
        pw.writeInt(max);

        return pw.createPacket();
    }

    public static Packet getSimpleMessagePacket(int npc, int speaker, String text) {
        return getMessagePacket(npc, ConversationType.ASK_MENU, speaker, text).createPacket();
    }

    public static Packet getAcceptMessagePacket(int npc, int speaker, String text) {
        return getMessagePacket(npc, ConversationType.ASK_ACCEPT, speaker, text).createPacket();
    }

    public static Packet getBoxTextMessagePacket(int npc, int speaker, String def, int cols, int rows) {
        PacketWriter pw = getMessagePacket(npc, ConversationType.ASK_BOX_TEXT, speaker, "");
        pw.writeMapleString(def);
        pw.writeShort(cols);
        pw.writeShort(rows);

        return pw.createPacket();
    }

    /**
     * Very weird one, dimensional portal in gms I think
     */
    public static Packet getSlideMenuMessagePacket(int npc, int speaker, String text, int type, int selected) {
        PacketWriter pw = new PacketWriter(16);

        pw.writeHeader(SendOpcode.SCRIPT_MESSAGE);
        pw.write(0); // SpeakerTypeID
        pw.writeInt(npc);
        pw.write(ConversationType.ASK_SLIDE_MENU.getValue());
        pw.write(speaker);

        pw.writeInt(type);
        pw.writeInt(selected);
        pw.writeMapleString(text);

        return pw.createPacket();
    }
}
