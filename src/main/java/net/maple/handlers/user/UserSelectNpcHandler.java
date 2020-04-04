package net.maple.handlers.user;

import client.Character;
import client.Client;
import field.object.FieldObjectType;
import field.object.life.FieldNPC;
import net.maple.handlers.PacketHandler;
import util.packet.PacketReader;

public class UserSelectNpcHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        int npcObjectId = reader.readInteger();

        FieldNPC npc = (FieldNPC) chr.getField().getObjects(FieldObjectType.NPC)
                .stream().filter(o -> o.getId() == npcObjectId).findFirst().orElse(null);
        if (npc != null) {
            System.out.println("[UserSelectNpcHandler] " + npc.getName() + " (" + npc.getNpcId() + ")");
        }
    }
}
