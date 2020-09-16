package net.maple.handlers.user;

import client.Character;
import client.Client;
import field.obj.FieldObjectType;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class UserGivePopularityRequestHandler extends PacketHandler {

    private enum GivePopularityRes {
        SUCCESS(0x00),
        INVALID_CHARACTER_ID(0x01),
        LEVEL_LOW(0x02),
        ALREADY_DONE_TODAY(0x03),
        ALREADY_DONE_TARGET(0x04),
        NOTIFY(0x05),
        UNKNOWN_ERROR(0xFFFFFFFF);

        private final int value;

        GivePopularityRes(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character sender = c.getCharacter();
        int cid = reader.readInteger();
        if (cid == sender.getId()) {
            c.close(this, c.getCharacter().getName() + " tried to fame themselves");
            return;
        }
        if (sender.getLevel() < 15) {
            c.write(getLevelLowPacket());
            return;
        }

        byte fame = reader.readByte();
        Character receiver = (Character) sender.getField().getObject(FieldObjectType.CHARACTER, cid);
        if (receiver == null) { // receiver is not on the same map (anymore)
            c.write(getInvalidCharacterIdPacket());
            return;
        }
        if (fame < 0 || fame > 1) {
            c.close(this, "Invalid byte");
            return;
        }
        if (fame == 0) {
            receiver.defame();
        } else {
            receiver.fame();
        }

        c.write(getSuccessPacket(receiver, fame));
        receiver.write(getNotifyPacket(sender, fame));
    }

    private PacketWriter getSendPopPacket(GivePopularityRes operation) {
        PacketWriter pw = new PacketWriter(8);

        pw.writeHeader(SendOpcode.GIVE_POPULARITY_RESULT);
        pw.write(operation.getValue());

        return pw;
    }

    private Packet getSuccessPacket(Character target, int fame) {
        PacketWriter pw = getSendPopPacket(GivePopularityRes.SUCCESS);

        pw.writeMapleString(target.getName());
        pw.write(fame);
        pw.writeInt(target.getFame()); // nPop

        return pw.createPacket();
    }

    private Packet getInvalidCharacterIdPacket() {
        PacketWriter pw = getSendPopPacket(GivePopularityRes.INVALID_CHARACTER_ID);
        return pw.createPacket();
    }

    private Packet getLevelLowPacket() {
        PacketWriter pw = getSendPopPacket(GivePopularityRes.LEVEL_LOW);
        return pw.createPacket();
    }

    // todo
    private Packet getAlreadyDoneTodayPacket() {
        PacketWriter pw = getSendPopPacket(GivePopularityRes.ALREADY_DONE_TODAY);
        return pw.createPacket();
    }

    // todo
    private Packet getAlreadyDoneTargetPacket() {
        PacketWriter pw = getSendPopPacket(GivePopularityRes.ALREADY_DONE_TARGET);
        return pw.createPacket();
    }

    private Packet getNotifyPacket(Character sender, int fame) {
        PacketWriter pw = getSendPopPacket(GivePopularityRes.NOTIFY);

        pw.writeMapleString(sender.getName());
        pw.write(fame);

        return pw.createPacket();
    }
}
