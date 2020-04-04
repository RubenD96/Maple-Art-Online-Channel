package net.maple.handlers.misc;

import client.Character;
import client.Client;
import field.object.life.FieldControlledObject;
import field.object.life.FieldNPC;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class NPCMoveHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        int npcObjectId = reader.readInteger();

        FieldControlledObject npc = chr.getField().getControlledObject(chr, npcObjectId);
        if (npc != null) {
            chr.getField().broadcast(moveNPC((FieldNPC) npc, reader));
        }
    }

    public static Packet moveNPC(FieldNPC npc, PacketReader r) {
        PacketWriter pw = new PacketWriter(8);

        pw.writeHeader(SendOpcode.NPC_MOVE);
        pw.writeInt(npc.getId());
        pw.writeByte(r.readByte());
        pw.writeByte(r.readByte());

        if (npc.isMove()) {
            npc.move(r).encode(pw);
        }

        return pw.createPacket();
    }
}
