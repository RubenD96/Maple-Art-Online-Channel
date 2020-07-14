package net.maple.handlers.user;

import client.Client;
import net.maple.handlers.PacketHandler;
import scripting.npc.ConversationType;
import scripting.npc.NPCConversationManager;
import scripting.npc.NPCScriptManager;
import util.HexTool;
import util.packet.PacketReader;

public class UserScriptMessageAnswerHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        //System.out.println(HexTool.toHex(reader.getData()));
        byte type = reader.readByte();
        byte action = reader.readByte(); // 1 = continue, 255 = end chat

        NPCConversationManager cm = NPCScriptManager.getInstance().getCms().get(c);
        if (cm != null) {
            int selection = -1;
            if (type == ConversationType.ASK_MENU.getValue() || type == ConversationType.ASK_NUMBER.getValue()) {
                selection = reader.readInteger();
            }
            if ((type == ConversationType.ASK_TEXT.getValue() || type == ConversationType.ASK_BOX_TEXT.getValue()) && action == 1) {
                cm.setText(reader.readMapleString());
            }
            NPCScriptManager.getInstance().start(c, action, selection);
        }
    }
}
