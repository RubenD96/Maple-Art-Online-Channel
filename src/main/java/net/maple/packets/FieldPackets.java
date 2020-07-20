package net.maple.packets;

import client.Character;
import field.object.FieldObject;
import field.object.drop.AbstractFieldDrop;
import field.object.drop.EnterType;
import net.maple.SendOpcode;
import util.packet.Packet;
import util.packet.PacketWriter;

public class FieldPackets {

    public static Packet setField(Character chr) {
        boolean isInstantiated = false;
        PacketWriter pw = new PacketWriter(32); // mhm hard to know

        pw.writeHeader(SendOpcode.SET_FIELD);
        pw.writeShort(0);
        pw.writeInt(chr.getChannel().getChannelId());
        pw.writeInt(0); // world

        pw.writeBool(true);
        pw.writeBool(!isInstantiated); // instantiated
        pw.writeShort(0);

        if (!isInstantiated) {
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);

            CharacterPackets.encodeData(chr, pw);

            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
        } else {
            System.err.println("[SetField] uuuh?");
        }

        pw.writeLong(System.currentTimeMillis() * 10000 + 116444592000000000L);

        return pw.createPacket();
    }

    public static Packet enterField(Character chr) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.USER_ENTER_FIELD);
        pw.writeInt(chr.getId()); // obj id

        pw.write(chr.getLevel());
        pw.writeMapleString(chr.getName());

        // guild
        pw.writeMapleString("");
        pw.writeShort(0);
        pw.write(0);
        pw.writeShort(0);
        pw.write(0);

        // temp stats
        // masks
        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(0);
        // nDefenseAtt & nDefenseState
        pw.write(0);
        pw.write(0);

        pw.writeShort(chr.getJob());

        CharacterPackets.encodeLooks(pw, chr, false);

        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(0); // complete set itemid
        pw.writeInt(chr.getPortableChair() == null ? 0 : chr.getPortableChair());

        pw.writePosition(chr.getPosition());
        pw.write(chr.getMoveAction());
        pw.writeShort(chr.getFoothold());
        pw.write(0); // ?

        // pets here

        // a whole bunch of ?
        pw.writeBool(false);
        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(0);
        pw.write(0);
        pw.writeBool(false);
        pw.writeBool(false);
        pw.writeBool(false);
        pw.writeBool(false);
        pw.write(0);
        pw.write(0);
        pw.writeInt(0);

        return pw.createPacket();
    }

    public static Packet leaveField(Character chr) {
        PacketWriter pw = new PacketWriter(6);

        pw.writeHeader(SendOpcode.USER_LEAVE_FIELD);
        pw.writeInt(chr.getId());

        return pw.createPacket();
    }

    public static Packet enterField(AbstractFieldDrop drop, byte enterType) {
        PacketWriter pw = new PacketWriter(32);
        //byte type = drop.getEnterType();
        FieldObject source = drop.getSource();

        pw.writeHeader(SendOpcode.DROP_ENTER_FIELD);
        pw.write(enterType);
        pw.writeInt(drop.getId());
        pw.writeBool(drop.isMeso());
        pw.writeInt(drop.getInfo());
        pw.writeInt(/*drop.getOwner()*/0);
        pw.write(/*type*/0x02); // own type
        pw.writePosition(drop.getPosition());
        pw.writeInt(source instanceof Character ? 0 : source.getId()); // source

        if (enterType != EnterType.FFA) {
            pw.writePosition(source.getPosition());
            pw.writeShort(0); // delay
        }

        if (!drop.isMeso()) {
            pw.writeLong(/*drop.getExpire() * 10000 + 116444592000000000L*/0);
        }

        pw.writeBool(false);
        pw.writeBool(false);

        return pw.createPacket();
    }

    public static Packet leaveField(AbstractFieldDrop drop) {
        return leaveField(drop, null);
    }

    public static Packet leaveField(AbstractFieldDrop drop, FieldObject source) {
        PacketWriter pw = new PacketWriter(14);

        pw.writeHeader(SendOpcode.DROP_LEAVE_FIELD);
        pw.write(drop.getLeaveType()); // nLeaveType
        pw.writeInt(drop.getId());

        if (drop.getLeaveType() == 0x02 || drop.getLeaveType() == 0x03 || drop.getLeaveType() == 0x05) {
            pw.writeInt(source == null ? 0 : source.getId());
        } else if (drop.getLeaveType() == 0x04) {
            pw.writeShort(0);
        }

        return pw.createPacket();
    }
}
