package net.maple.packets

import client.inventory.item.slots.ItemSlot
import client.inventory.item.slots.ItemSlotBundle
import client.inventory.item.slots.ItemSlotEquip
import client.inventory.item.slots.ItemSlotPet
import constants.ItemConstants.isRechargeableItem
import util.packet.PacketWriter

object ItemPackets {

    fun ItemSlot.encode(pw: PacketWriter) {
        when (this) {
            is ItemSlotEquip -> {
                encode(this, pw)
            }
            is ItemSlotBundle -> {
                encode(this, pw)
            }
            is ItemSlotPet -> {
                encode(this, pw)
            }
        }
    }

    private fun encodeBase(item: ItemSlot, pw: PacketWriter) {
        pw.writeInt(item.templateId)
        pw.writeBool(item.cashItemSN > 0)

        if (item.cashItemSN > 0) {
            pw.writeLong(item.cashItemSN)
        }

        pw.writeLong(if (item.expire > 0) item.expire else 150842304000000000L)
    }

    private fun encode(item: ItemSlotEquip, pw: PacketWriter) {
        pw.write(1)
        encodeBase(item, pw)
        pw.write(item.ruc.toInt())
        pw.write(item.cuc.toInt())

        pw.writeShort(item.str)
        pw.writeShort(item.dex)
        pw.writeShort(item.int)
        pw.writeShort(item.luk)
        pw.writeShort(item.maxHP)
        pw.writeShort(item.maxMP)
        pw.writeShort(item.pad)
        pw.writeShort(item.mad)
        pw.writeShort(item.pdd)
        pw.writeShort(item.mdd)
        pw.writeShort(item.acc)
        pw.writeShort(item.eva)

        pw.writeShort(item.craft)
        pw.writeShort(item.speed)
        pw.writeShort(item.jump)
        pw.writeMapleString(item.title)
        pw.writeShort(item.attribute)

        pw.write(item.levelUpType.toInt())
        pw.write(item.level.toInt())
        pw.writeInt(item.exp)
        pw.writeInt(item.durability)

        pw.writeInt(item.iuc)

        pw.write(item.grade.toInt())
        pw.write(item.chuc.toInt())

        pw.writeShort(item.option1)
        pw.writeShort(item.option2)
        pw.writeShort(item.option3)
        pw.writeShort(item.socket1)
        pw.writeShort(item.socket2)

        if (item.cashItemSN == 0L) pw.writeLong(0)
        pw.writeLong(0)
        pw.writeInt(0)
    }

    private fun encode(item: ItemSlotBundle, pw: PacketWriter) {
        pw.write(2)
        encodeBase(item, pw)

        pw.writeShort(item.number)
        pw.writeMapleString(item.title)
        pw.writeShort(item.attribute)

        if (isRechargeableItem(item.templateId)) {
            pw.writeLong(0)
        }
    }

    fun encode(item: ItemSlotPet, pw: PacketWriter) {
        pw.write(3)
        encodeBase(item, pw)

        pw.writeFixedString(item.petName, 13)
        pw.write(item.level.toInt())
        pw.writeShort(item.tameness)
        pw.write(item.repleteness.toInt())
        pw.writeLong(item.dateDead)

        pw.writeShort(item.petAttribute)
        pw.writeShort(item.petSkill)
        pw.writeInt(item.remainLife)
        pw.writeShort(item.attribute)
    }
}