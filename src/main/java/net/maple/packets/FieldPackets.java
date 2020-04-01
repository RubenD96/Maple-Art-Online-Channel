package net.maple.packets;

import field.object.FieldObject;
import field.object.drop.AbstractFieldDrop;
import field.object.drop.EnterType;
import net.maple.SendOpcode;
import client.Character;
import util.packet.Packet;
import util.packet.PacketWriter;

import java.awt.*;

public class FieldPackets {

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
        pw.writeInt(0); // portable chair

        pw.writePosition(new Point(-235, 179));
        pw.write(4); // move action
        pw.writeShort(0); // foothold
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

    public static Packet enterField(AbstractFieldDrop drop) {
        PacketWriter pw = new PacketWriter(32);
        byte type = drop.getEnterType();
        FieldObject source = drop.getSource();

        pw.writeHeader(SendOpcode.DROP_ENTER_FIELD);
        pw.write(type);
        pw.writeInt(drop.getId());
        pw.writeBool(drop.isMeso());
        pw.writeInt(drop.getInfo());
        pw.writeInt(drop.getOwner());
        pw.write(type); // own type
        pw.writePosition(drop.getPosition());
        pw.writeInt(source instanceof Character ? 0 : source.getId()); // source

        if (type != EnterType.FFA) {
            pw.writePosition(source.getPosition());
            pw.writeShort(0); // delay
        }

        if (!drop.isMeso()) {
            pw.writeLong(drop.getExpire() * 10000 + 116444592000000000L);
        }

        pw.writeBool(false);
        pw.writeBool(false);

        return pw.createPacket();
    }

    public static Packet leaveField(AbstractFieldDrop drop) {
        PacketWriter pw = new PacketWriter(32);
        byte leaveType = 1;

        pw.writeHeader(SendOpcode.DROP_LEAVE_FIELD);
        pw.write(leaveType); // animation or something
        pw.writeInt(drop.getId());

        if (leaveType == 0x02 || leaveType == 0x03 || leaveType == 0x05) {
            pw.writeInt(drop.getSource().getId());
        } else if (leaveType == 0x04) {
            pw.writeShort(0);
        }

        return pw.createPacket();
    }
}
