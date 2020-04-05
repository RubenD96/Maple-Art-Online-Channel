package net.maple.packets;

import client.inventory.slots.ItemSlot;
import client.inventory.slots.ItemSlotBundle;
import client.inventory.slots.ItemSlotEquip;
import client.inventory.slots.ItemSlotPet;
import constants.ItemConstants;
import util.packet.PacketWriter;

public class ItemPackets {

    public static void encode(ItemSlot item, PacketWriter pw) {
        if (item instanceof ItemSlotEquip) {
            encode((ItemSlotEquip) item, pw);
        } else if (item instanceof ItemSlotBundle) {
            encode((ItemSlotBundle) item, pw);
        } else if (item instanceof ItemSlotPet) {
            encode((ItemSlotPet) item, pw);
        }
    }

    private static void encodeBase(ItemSlot item, PacketWriter pw) {
        pw.writeInt(item.getTemplateId());
        pw.writeBool(item.getCashItemSN() > 0);
        if (item.getCashItemSN() > 0) {
            pw.writeLong(item.getCashItemSN());
        }
        pw.writeLong(item.getExpire() > 0 ? item.getExpire() : 0);
    }

    private static void encode(ItemSlotEquip item, PacketWriter pw) {
        pw.writeByte((byte) 1);
        encodeBase(item, pw);
        pw.writeByte(item.getRUC());
        pw.writeByte(item.getCUC());

        pw.writeShort(item.getSTR());
        pw.writeShort(item.getDEX());
        pw.writeShort(item.getINT());
        pw.writeShort(item.getLUK());
        pw.writeShort(item.getMaxHP());
        pw.writeShort(item.getMaxMP());
        pw.writeShort(item.getPAD());
        pw.writeShort(item.getMAD());
        pw.writeShort(item.getPDD());
        pw.writeShort(item.getMDD());
        pw.writeShort(item.getACC());
        pw.writeShort(item.getEVA());

        pw.writeShort(item.getCraft());
        pw.writeShort(item.getSpeed());
        pw.writeShort(item.getJump());
        pw.writeMapleString(item.getTitle());
        pw.writeShort(item.getAttribute());

        pw.writeByte(item.getLevelUpType());
        pw.writeByte(item.getLevel());
        pw.writeInt(item.getEXP());
        pw.writeInt(item.getDurability());

        pw.writeByte(item.getGrade());
        pw.writeByte(item.getCHUC());

        pw.writeShort(item.getOption1());
        pw.writeShort(item.getOption2());
        pw.writeShort(item.getOption3());
        pw.writeShort(item.getSocket1());
        pw.writeShort(item.getSocket2());

        if (item.getCashItemSN() == 0) pw.writeLong(0);
        pw.writeLong(0);
        pw.writeInt(0);
    }

    private static void encode(ItemSlotBundle item, PacketWriter pw) {
        pw.writeByte((byte) 2);
        encodeBase(item, pw);

        pw.writeShort(item.getNumber());
        pw.writeMapleString(item.getTitle());
        pw.writeShort(item.getAttribute());

        if (ItemConstants.isRechargeableItem(item.getTemplateId())) {
            pw.writeLong(0);
        }
    }

    public static void encode(ItemSlotPet item, PacketWriter pw) {
        pw.writeByte((byte) 3);
        encodeBase(item, pw);

        pw.writeString(item.getPetName());
        pw.fill(0x00, 13 - item.getPetName().length());

        pw.writeByte(item.getLevel());
        pw.writeShort(item.getTameness());
        pw.writeByte(item.getRepleteness());
        pw.writeLong(item.getDateDead());

        pw.writeShort(item.getPetAttribute());
        pw.writeShort(item.getPetSkill());
        pw.writeInt(item.getRemainLife());
        pw.writeShort(item.getAttribute());
    }
}
