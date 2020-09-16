package net.maple.handlers.misc;

import client.Character;
import client.Client;
import field.Field;
import field.obj.life.FieldControlledObject;
import field.obj.life.FieldNPC;
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

        Field field = chr.getField();

        if (field != null) { // might happen anytime user leaves the field but client still sent the packet when field was already set to null server sided
            FieldControlledObject npc = field.getControlledObject(chr, npcObjectId);
            if (npc instanceof FieldNPC) {
                field.broadcast(moveNPC((FieldNPC) npc, reader));
            }
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
