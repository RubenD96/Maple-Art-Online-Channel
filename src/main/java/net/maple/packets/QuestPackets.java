package net.maple.packets;

import net.maple.SendOpcode;
import util.packet.Packet;
import util.packet.PacketWriter;

public class QuestPackets {

    public static Packet getStartQuestPacket(int qid, int nid) {
        PacketWriter pw = new PacketWriter(5);

        pw.writeHeader(SendOpcode.USER_QUEST_RESULT);
        pw.write(0x0A); // QUESTRES_ACT_SUCCESS
        pw.writeShort(qid);
        pw.writeInt(nid);
        pw.writeInt(0); // nextQuest

        return pw.createPacket();
    }
}
