package net.maple.handlers.user;

import client.Client;
import net.maple.handlers.PacketHandler;
import scripting.npc.NPCScriptManager;
import util.HexTool;
import util.packet.PacketReader;

public class UserScriptMessageAnswerHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        System.out.println(HexTool.toHex(reader.getData()));
        byte type = reader.readByte();
        byte action = reader.readByte(); // 1 = continue, 255 = end chat

        if (NPCScriptManager.getInstance().getCms().get(c) != null) {
            NPCScriptManager.getInstance().start(c, action, -1);
        }
    }
}
