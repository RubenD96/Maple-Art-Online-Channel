package net.maple.handlers.misc;

import client.Client;
import field.object.life.FieldControlledObject;
import field.object.life.FieldMob;
import field.object.life.FieldNPC;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import java.util.stream.IntStream;

public class MobMoveHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        int id = reader.readInteger();
        FieldControlledObject obj = c.getCharacter().getField().getControlledObject(c.getCharacter(), id);
        if (obj == null) {
            return;
        }
        if (obj instanceof FieldNPC) {
            System.err.println("[MobMoveHandler] Packet sent from FieldNPC object: " + id);
            return;
        }

        FieldMob mob = (FieldMob) obj;
        short mobCtrlSN = reader.readShort();
        boolean mobMove = (reader.readByte() & 0xF) != 0;
        byte split = reader.readByte();
        int velocity = reader.readInteger();
        reader.readByte();

        int multiTargetForBall = reader.readInteger(); // wtf
        IntStream.range(0, multiTargetForBall).forEach(i -> reader.readLong());

        int areaAttack = reader.readInteger();
        IntStream.range(0, areaAttack).forEach(i -> reader.readInteger());

        reader.readInteger();
        reader.readInteger();
        reader.readInteger();
        reader.readInteger();

        mob.getController().write(sendMobControl(mob, mobCtrlSN, mobMove));
        mob.getField().broadcast(sendMobMovement(reader, mob, mobMove, split, velocity), mob.getController());
    }

    private static Packet sendMobControl(FieldMob mob, short SN, boolean move) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.MOB_CTRL_ACK);
        pw.writeInt(mob.getId());
        pw.writeShort(SN);
        pw.writeBool(move);
        pw.writeShort(0); // nMP
        pw.write(0); // skill command
        pw.write(0); // slv?

        return pw.createPacket();
    }

    private static Packet sendMobMovement(PacketReader r, FieldMob mob, boolean move, byte split, int velocity) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.MOB_MOVE);
        pw.writeInt(mob.getId());
        pw.writeBool(move);
        pw.write(split);
        pw.write(0);
        pw.write(0);
        pw.writeInt(velocity);
        pw.writeInt(0);
        pw.writeInt(0);

        mob.move(r).encode(pw);

        return pw.createPacket();
    }
}
