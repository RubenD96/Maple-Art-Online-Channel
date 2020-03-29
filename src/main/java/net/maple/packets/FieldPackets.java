package net.maple.packets;

import net.maple.SendOpcode;
import player.Character;
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
}
