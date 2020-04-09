package net.maple.packets;

import client.Character;
import client.Pet;
import client.inventory.ItemInventory;
import client.inventory.ItemInventoryType;
import client.inventory.ModifyInventoriesContext;
import client.inventory.operations.MoveInventoryOperation;
import client.inventory.slots.ItemSlot;
import client.messages.Message;
import client.player.StatType;
import net.maple.SendOpcode;
import util.packet.Packet;
import util.packet.PacketWriter;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CharacterPackets {

    public static void encodeData(Character chr, PacketWriter pw) {
        pw.writeLong(-1); // flags
        pw.write(0);
        pw.write(0);

        encodeStats(pw, chr, true);
        pw.write(250); // friends
        pw.writeBool(false);

        pw.writeInt(chr.getMeso());

        // inv slots
        pw.write(chr.getInventories().get(ItemInventoryType.EQUIP).getSlotMax()); // equips
        pw.write(chr.getInventories().get(ItemInventoryType.CONSUME).getSlotMax()); // consumes
        pw.write(chr.getInventories().get(ItemInventoryType.INSTALL).getSlotMax()); // install
        pw.write(chr.getInventories().get(ItemInventoryType.ETC).getSlotMax()); // etc
        pw.write(chr.getInventories().get(ItemInventoryType.CASH).getSlotMax()); // cash

        // admin shop
        pw.writeInt(0);
        pw.writeInt(0);

        // equips
        Map<ItemInventoryType, ItemInventory> inventories = chr.getInventories();
        var inventory = inventories.get(ItemInventoryType.EQUIP).getItems();
        var equip = inventory.entrySet().stream()
                .filter(kv -> kv.getKey() >= 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        var equipped = inventory.entrySet().stream()
                .filter(kv -> kv.getKey() >= -100 && kv.getKey() < 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        var mask = inventory.entrySet().stream()
                .filter(kv -> kv.getKey() >= -1000 && kv.getKey() < -100)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        var dragon = inventory.entrySet().stream()
                .filter(kv -> kv.getKey() >= -1100 && kv.getKey() < -1000)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        var mech = inventory.entrySet().stream()
                .filter(kv -> kv.getKey() >= -1200 && kv.getKey() < -1100)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        new ArrayList<>(Arrays.asList(equipped, mask, equip, dragon, mech))
                .forEach(inv -> {
                    inv.forEach((slot, item) -> {
                        pw.writeShort(Math.abs(slot) % 100);
                        ItemPackets.encode(item, pw);
                    });
                    pw.writeShort(0);
                });

        // other inv's
        new ArrayList<>(Arrays.asList(
                inventories.get(ItemInventoryType.CONSUME).getItems(),
                inventories.get(ItemInventoryType.INSTALL).getItems(),
                inventories.get(ItemInventoryType.ETC).getItems(),
                inventories.get(ItemInventoryType.CASH).getItems()
        )).forEach(
                inv -> {
                    inv.forEach((slot, item) -> {
                        pw.write(slot);
                        ItemPackets.encode(item, pw);
                    });
                    pw.write(0);
                }
        );

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
        pw.writeInt(chr.getField().getId());
        pw.write(chr.getPortal());
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
        Map<Short, ItemSlot> equips = chr.getInventories().get(ItemInventoryType.EQUIP)
                .getItems().entrySet().stream()
                .filter(kv -> kv.getKey() < 0)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
        Map<Short, ItemSlot> base = equips.entrySet().stream()
                .map(kv -> equips.containsKey((short) (kv.getKey() - 100))
                        ? new AbstractMap.SimpleEntry<>(kv.getKey(), equips.get((short) (kv.getKey() - 100)))
                        : kv)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
        Map<Short, ItemSlot> mask = equips.entrySet().stream()
                .filter(kv -> !base.entrySet().contains(kv))
                .collect(Collectors.toMap(
                        kv -> (short) (kv.getKey() <= -100 ? kv.getKey() - 100 : kv.getKey()),
                        Map.Entry::getValue
                ));

        base.forEach((k, v) -> pw.write(Math.abs(k)).writeInt(v.getTemplateId()));
        pw.write(0xFF);
        mask.forEach((k, v) -> pw.write(Math.abs(k)).writeInt(v.getTemplateId()));
        pw.write(0xFF);
        pw.writeInt(equips.containsKey((short) -111) ? equips.get((short) -111).getTemplateId() : 0);
    }

    /*public static void statUpdate(Character chr, List<StatType> statTypes) {
        statUpdate(chr, statTypes, true);
    }*/

    public static void statUpdate(Character chr, List<StatType> statTypes, boolean enableActions) {
        PacketWriter pw = new PacketWriter(32);

        if (statTypes.size() > 1) {
            Collections.sort(statTypes);
        }

        pw.writeHeader(SendOpcode.STAT_CHANGED);
        pw.writeBool(enableActions);

        int flag = statTypes.stream().mapToInt(StatType::getStat).reduce(0, (a, b) -> a | b);
        pw.writeInt(flag);

        statTypes.forEach(statType -> {
            switch (statType) {
                case SKIN:
                    pw.write(chr.getSkinColor());
                    break;
                case FACE:
                    pw.writeInt(chr.getFace());
                    break;
                case HAIR:
                    pw.writeInt(chr.getHair());
                    break;
                case PET:
                case PET2:
                case PET3:
                case TEMP_EXP:
                    System.err.println("[statUpdate] unimplemented " + statType.name());
                    break;
                case LEVEL:
                    pw.write(chr.getLevel());
                    break;
                case JOB:
                    pw.writeShort(chr.getJob());
                    break;
                case STR:
                    pw.writeShort(chr.getStrength());
                    break;
                case DEX:
                    pw.writeShort(chr.getDexterity());
                    break;
                case INT:
                    pw.writeShort(chr.getIntelligence());
                    break;
                case LUK:
                    pw.writeShort(chr.getLuck());
                    break;
                case MAX_HP:
                    pw.writeInt(chr.getMaxHealth());
                    break;
                case HP:
                    pw.writeInt(chr.getHealth());
                    break;
                case MAX_MP:
                    pw.writeInt(chr.getMaxMana());
                    break;
                case MP:
                    pw.writeInt(chr.getMana());
                    break;
                case AP:
                    pw.writeShort(chr.getAp());
                    break;
                case SP:
                    pw.writeShort(chr.getSp());
                    break;
                case EXP:
                    pw.writeInt(chr.getExp());
                    break;
                case FAME:
                    pw.writeShort(chr.getFame());
                    break;
                case MESO:
                    pw.writeInt(chr.getMeso());
                    break;
                default:
                    System.err.println("[statUpdate] unimplemented " + statType.name());
                    break;
            }
        });

        pw.writeBool(false);
        pw.writeBool(false);

        chr.write(pw.createPacket());

        if (statTypes.stream().anyMatch(type -> type == StatType.SKIN ||
                type == StatType.FACE ||
                type == StatType.HAIR)) {
            modifyAvatar(chr);
        }
    }

    public static void modifyInventory(Character chr, Consumer<ModifyInventoriesContext> consumer, boolean enableActions) {
        ModifyInventoriesContext context = new ModifyInventoriesContext(chr.getInventories());

        consumer.accept(context);
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.INVENTORY_OPERATION);
        pw.writeBool(enableActions);
        context.encode(pw);
        pw.writeBool(false);

        chr.write(pw.createPacket());

        // equip check
        if (context.getOperations().stream().anyMatch(op -> op.getSlot() < 0) ||
                context.getOperations().stream().filter(op -> op instanceof MoveInventoryOperation).anyMatch(mio -> ((MoveInventoryOperation) mio).getToSlot() < 0)) {
            chr.validateStats();
            modifyAvatar(chr);
        }
    }

    public static void modifyAvatar(Character chr) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.USER_AVATAR_MODIFIED);
        pw.writeInt(chr.getId());
        pw.write(0x01); // some flag
        encodeLooks(pw, chr, false);

        pw.writeBool(false);
        pw.writeBool(false);
        pw.writeBool(false);
        pw.writeBool(false);
        pw.writeInt(0); // set items

        chr.getField().broadcast(pw.createPacket(), chr);
    }

    public static void showDamage(Character chr, byte type, int dmg, int mobId, byte dir) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.USER_HIT);
        pw.writeInt(chr.getId());
        pw.write(type);

        pw.writeInt(dmg);
        if (type > -2) {
            pw.writeInt(mobId);
            pw.write(dir);
            pw.write(0); // ?
            pw.write(0); // power guard
            pw.writeBool(false); // stance
        }
        pw.writeInt(dmg);
        /*byte v5 = 0;
        pw.write(v5);
        pw.writeInt(chr.getId()); // v61
        if (v5 > -2) {
            pw.writeInt(mobId); // v51
            pw.write(1); // v48, unsure
            byte v19 = 0;
            pw.write(v19); // v19 powerguard?
            if (v19 > 0) {
                pw.writeBool(true); // v49
                pw.writeInt(oid); // v51

                pw.write(1); // v27 ?
                *//*pw.writeShort(0);
                pw.writeShort(0);*//*
                pw.writePosition(chr.getPosition()); // v51 + v28
            }
            pw.write(0); // v4
            pw.write(0); // v34 skill
        }
        pw.writeInt(dmg); // result*/


        chr.getField().broadcast(pw.createPacket(), chr);
    }

    public static Packet message(Message message) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.MESSAGE);
        message.encode(pw);

        return pw.createPacket();
    }
}
