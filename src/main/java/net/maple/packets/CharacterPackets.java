package net.maple.packets;

import client.Character;
import client.Pet;
import util.packet.PacketWriter;

import java.util.HashMap;
import java.util.Map;

public class CharacterPackets {

    public static void encodeData(Character chr, PacketWriter pw) {
        pw.writeLong(-1); // flags
        pw.write(0);
        pw.write(0);

        encodeStats(pw, chr, true);
        pw.write(250); // friends
        pw.writeBool(false);

        pw.writeInt(100); // meso?

        // inv slots
        pw.write(24); // equips
        pw.write(24); // consumes
        pw.write(24); // install
        pw.write(24); // etc
        pw.write(96); // cash

        // admin shop
        pw.writeInt(0);
        pw.writeInt(0);

        // equips
        pw.writeShort(0); // equipped
        pw.writeShort(0); // equipped2
        pw.writeShort(0); // equip (inventory tab)
        pw.writeShort(0); // dragon
        pw.writeShort(0); // mechanic

        // other inv's
        pw.write(0); // consume
        pw.write(0); // install
        pw.write(0); // etc
        pw.write(0); // cash

        // skills
        pw.writeShort(0); // count

        // skill cooldowns
        pw.writeShort(0);

        // quests
        pw.writeShort(0); // active count
        pw.writeShort(0); // completed count

        // minigames
        pw.writeShort(0);

        // couples?
        pw.writeShort(0); // couple
        pw.writeShort(0); // friend
        pw.writeShort(0); // marriage

        // uuuh
        for (int i = 0; i < 15; i++) {
            pw.writeInt(0);
        }

        // new year card
        pw.writeShort(0);

        // more quests?
        pw.writeShort(0); // count

        // wild hunter data (probably wont use this
        if (chr.getJob().getId() / 100 == 33) {
            pw.write(0);
            for (int i = 0; i < 5; i++) {
                pw.writeInt(0);
            }
        }

        pw.writeShort(0); // quest complete old?
        pw.writeShort(0); // visitor log
    }

    private static void encodeStats(final PacketWriter pw, Character chr, boolean ingame) {
        pw.writeInt(chr.getId()); // character id
        pw.writeString(chr.getName());
        pw.fill(0x00, 13 - chr.getName().length());
        pw.write(chr.getGender());
        pw.write(chr.getSkinColor()); // skin color
        pw.writeInt(chr.getFace()); // face
        pw.writeInt(chr.getHair()); // hair

        for (Pet pet : chr.getPets()) {
            if (pet != null) {
                pw.writeLong(pet.getId());
            } else {
                pw.writeLong(0);
            }
        }

        pw.write(chr.getLevel());
        pw.writeShort(chr.getJob());
        pw.writeShort(chr.getStrength());
        pw.writeShort(chr.getDexterity());
        pw.writeShort(chr.getIntelligence());
        pw.writeShort(chr.getLuck());
        pw.writeInt(chr.getHealth());
        pw.writeInt(chr.getMaxHealth());
        pw.writeInt(chr.getMana());
        pw.writeInt(chr.getMaxMana());
        pw.writeShort(chr.getAp());
        pw.writeShort(ingame ? chr.getSp() : 0);
        pw.writeInt(chr.getExp());
        pw.writeShort(chr.getFame());
        pw.writeInt(0); // Gacha Exp
        pw.writeInt(chr.getFieldId());
        pw.write(chr.getSpawnpoint());
        pw.writeInt(0); // playtime
        pw.writeShort(0); // subjob?
    }

    public static void encodeLooks(PacketWriter pw, Character chr, boolean mega) {
        pw.write(chr.getGender());
        pw.write(chr.getSkinColor());
        pw.writeInt(chr.getFace());
        pw.writeBool(mega);
        pw.writeInt(chr.getHair());
        encodeVisualEquips(pw, chr);

        for (Pet pet : chr.getPets()) {
            if (pet != null) {
                pw.writeInt(pet.getItem());
            } else {
                pw.writeInt(0);
            }
        }
    }

    private static void encodeVisualEquips(final PacketWriter pw, Character chr) {
        Map<Byte, Integer> base = new HashMap<>();
        Map<Byte, Integer> mask = new HashMap<>();

        for (Map.Entry<Byte, Integer> item : chr.getEquipment().entrySet()) {
            byte pos = item.getKey();
            if (pos < 100 && !base.containsKey(pos)) {
                base.put(pos, item.getValue());
            } else if (pos > 100 && pos != 111) {
                pos -= 100;
                if (base.containsKey(pos)) {
                    mask.put(pos, base.get(pos));
                }
                base.put(pos, item.getValue());
            } else if (base.containsKey(pos)) {
                mask.put(pos, item.getValue());
            }
        }

        base.forEach((k, v) -> pw.write(k).writeInt(v));
        pw.write(0xFF);
        mask.forEach((k, v) -> pw.write(k).writeInt(v));
        pw.write(0xFF);
        pw.writeInt(chr.getEquipment().getOrDefault((byte) 111, 0));
    }
}
