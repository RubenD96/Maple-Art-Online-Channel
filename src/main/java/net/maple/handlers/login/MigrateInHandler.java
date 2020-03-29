package net.maple.handlers.login;

import net.database.CharacterAPI;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import net.maple.packets.CharacterPackets;
import player.Character;
import player.Client;
import player.field.KeyBinding;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import java.awt.*;

public class MigrateInHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        int cid = reader.readInteger();

        Character chr = CharacterAPI.getNewCharacter(c, cid);
        chr.setEquipment(CharacterAPI.getEquips(chr));
        c.setCharacter(chr);
        c.write(setField(chr));
        c.write(enterField(chr));
        /*c.write(initFuncKey(chr));
        c.write(initQuickslot(chr));*/
    }

    @Override
    public boolean validateState(Client c) {
        return true; // todo
    }

    private static Packet initFuncKey(Character chr) {
        PacketWriter pw = new PacketWriter(453);

        pw.writeHeader(SendOpcode.FUNC_KEY_MAPPED_INIT);
        pw.writeBool(false);
        for (int i = 0; i < 90; i++) {
            KeyBinding key = chr.getKeyBindings()[i];

            pw.write(key == null ? 0 : key.getType());
            pw.writeInt(key == null ? 0 : key.getAction());
        }

        return pw.createPacket();
    }

    private static Packet initQuickslot(Character chr) {
        PacketWriter pw = new PacketWriter(35);

        pw.writeHeader(SendOpcode.QUICKSLOT_MAPPED_INIT);
        pw.writeBool(true);
        for (int i = 0; i < 8; i++) {
            pw.writeInt(0);
        }

        return pw.createPacket();
    }

    private static Packet setField(Character chr) {
        boolean isInstantiated = false;
        PacketWriter pw = new PacketWriter(32); // mhm hard to know

        pw.writeHeader(SendOpcode.SET_FIELD);
        pw.writeShort(0);
        pw.writeInt(0); // channel?
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

    private static Packet enterField(Character chr) {
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
