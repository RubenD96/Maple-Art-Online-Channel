package net.maple.handlers.login;

import net.database.AccountAPI;
import net.database.CharacterAPI;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import net.maple.packets.CharacterPackets;
import player.Character;
import player.Client;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import java.util.Arrays;

import static net.maple.packets.FieldPackets.enterField;

public class MigrateInHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        c.login(AccountAPI.getAccountInfo("chronos"));

        int cid = reader.readInteger();

        Character chr = CharacterAPI.getNewCharacter(c, cid);
        chr.setEquipment(CharacterAPI.getEquips(chr));
        chr.setField(c.getWorldChannel().getFieldManager().getField(chr.getFieldId()));
        c.setCharacter(chr);
        c.write(setField(chr));
        c.write(enterField(chr));
        c.write(initFuncKey(chr));
        c.write(initQuickslot(chr));
    }

    @Override
    public boolean validateState(Client c) {
        return true; // todo
    }

    private static Packet initFuncKey(Character chr) {
        PacketWriter pw = new PacketWriter(453);

        pw.writeHeader(SendOpcode.FUNC_KEY_MAPPED_INIT);
        pw.writeBool(false);
        Arrays.stream(chr.getKeyBindings(), 0, 90).forEach(key -> {
            pw.write(key == null ? 0 : key.getType());
            pw.writeInt(key == null ? 0 : key.getAction());
        });

        return pw.createPacket();
    }

    private static Packet initQuickslot(Character chr) {
        PacketWriter pw = new PacketWriter(35);

        pw.writeHeader(SendOpcode.QUICKSLOT_MAPPED_INIT);
        pw.writeBool(true);
        for (int key : chr.getQuickSlotKeys()) {
            pw.writeInt(key);
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
}
