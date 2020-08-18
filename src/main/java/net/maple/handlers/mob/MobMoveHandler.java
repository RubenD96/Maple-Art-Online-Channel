package net.maple.handlers.mob;

import client.Client;
import field.Field;
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
        int oid = reader.readInteger();

        Field field = c.getCharacter().getField();

        if (field != null) { // might happen anytime user leaves the field but client still sent the packet when field was already set to null server sided
            FieldControlledObject obj = field.getControlledObject(c.getCharacter(), oid);
            if (obj == null) {
                return;
            }
            if (obj instanceof FieldNPC) {
                System.err.println("[MobMoveHandler] Packet sent from FieldNPC object: " + oid);
                return;
            }

            FieldMob mob = (FieldMob) obj;
            short mobCtrlSN = reader.readShort(); // moveid
            byte v7 = reader.readByte();
            boolean oldSplit = (v7 & 0xF0) != 0;
            boolean mobMoveStartResult = v7 > 0; // use skill //(v7 & 0xF) != 0;
            byte curSplit = reader.readByte(); // the skill
            int illegalVelocity = reader.readInteger();
            byte v8 = reader.readByte();

            boolean cheatedRandom = (v8 & 0xF0) != 0;
            boolean cheatedCtrlMove = (v8 & 0xF) != 0;

            int multiTargetForBall = reader.readInteger(); // wtf
            IntStream.range(0, multiTargetForBall).forEach(i -> reader.readLong());

            int randTimeForAreaAttack = reader.readInteger();
            IntStream.range(0, randTimeForAreaAttack).forEach(i -> reader.readInteger());

            int unk1 = reader.readInteger(); // HackedCode
            int unk2 = reader.readInteger();
            int unk3 = reader.readInteger(); // HackedCodeCrc
            int unk4 = reader.readInteger();

            mob.getController().write(sendMobControl(mob, mobCtrlSN, mobMoveStartResult));
            mob.getField().broadcast(sendMobMovement(reader, mob, mobMoveStartResult, curSplit, illegalVelocity), mob.getController());
        }
    }

    private static Packet sendMobControl(FieldMob mob, short mobCtrlSN, boolean mobMoveStartResult) {
        PacketWriter pw = new PacketWriter(13);

        pw.writeHeader(SendOpcode.MOB_CTRL_ACK);
        pw.writeInt(mob.getId());
        pw.writeShort(mobCtrlSN);
        pw.writeBool(mobMoveStartResult);
        pw.writeShort(0); // nMP
        pw.write(0); // skill command
        pw.write(0); // skill level

        return pw.createPacket();
    }

    private static Packet sendMobMovement(PacketReader r, FieldMob mob, boolean mobMoveStartResult, byte curSplit, int illegalVelocity) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.MOB_MOVE);
        pw.writeInt(mob.getId());
        pw.writeBool(false); // NotForceLandWhenDiscord
        pw.writeBool(mobMoveStartResult); // NotChangeAction
        pw.write(0); // NextAttackPossible
        pw.write(curSplit); // Left
        pw.writeInt(illegalVelocity); // dwTargetInfo

        pw.writeInt(0); // MultiTargetForBall (counter) if > 0, + int int
        pw.writeInt(0); // RandTimeForAreaAttack (counter) if > 0, + int

        mob.move(r).encode(pw);

        return pw.createPacket();
    }
}
