package net.maple.handlers.user;

import client.Client;
import client.player.quest.QuestRequest;
import net.maple.handlers.PacketHandler;
import scripting.quest.QuestConversationManager;
import scripting.quest.QuestScriptManager;
import util.HexTool;
import util.packet.PacketReader;

public class UserQuestRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        System.out.println("[UserQuestRequestHandler] " + HexTool.toHex(reader.getData()));

        byte action = reader.readByte();
        short questId = reader.readShort();
        int npcId = reader.readInteger();
        short ptUserPosX = reader.readShort();
        short ptUserPosY = reader.readShort();

        /*System.out.println("action: " + action);
        System.out.println("QuestId: " + questId);
        System.out.println("npcId: " + npcId);
        System.out.println("pos-x: " + ptUserPosX);
        System.out.println("pos-y: " + ptUserPosY);*/

        if (action == QuestRequest.OPENING_SCRIPT.getValue()) {
            System.out.println("start quest script");
            QuestScriptManager.getInstance().converse(c, npcId, questId, true);
        } else if (action == QuestRequest.COMPLETE_SCRIPT.getValue()) {
            System.out.println("end quest script");
            QuestScriptManager.getInstance().converse(c, npcId, questId, false);
        } else {
            System.out.println("Unknown/unhandled quest action (" + action + ")");
            if (!c.isAdmin()) {
                c.close(this, "Triggered unused quest action qid: " + questId + " - action: " + action);
            }
        }
    }
}
