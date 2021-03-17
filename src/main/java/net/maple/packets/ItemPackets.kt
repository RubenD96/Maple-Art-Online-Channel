package net.maple.packets

import client.inventory.item.slots.ItemSlot
import client.inventory.item.slots.ItemSlotBundle
import client.inventory.item.slots.ItemSlotEquip
import client.inventory.item.slots.ItemSlotPet
import constants.ItemConstants.isRechargeableItem
import constants.PacketConstants
import util.packet.PacketWriter

object ItemPackets {

    private const val ENCODE_EQUIP = 0x01
    private const val ENCODE_BUNDLE = 0x02
    private const val ENCODE_PET = 0x03

    fun ItemSlot.encode(pw: PacketWriter) {
        when (this) {
            is ItemSlotEquip -> {
                this.encode(pw)
            }
            is ItemSlotBundle -> {
                this.encode(pw)
            }
            is ItemSlotPet -> {
                this.encode(pw)
            }
        }
    }

    private fun ItemSlot.encodeBase(pw: PacketWriter) {
        pw.writeInt(templateId)
        pw.writeBool(cashItemSN > 0)

        if (cashItemSN > 0) {
            pw.writeLong(cashItemSN)
        }

        pw.writeLong(if (expire > 0) expire else PacketConstants.permanent)
    }

    private fun ItemSlotEquip.encode(pw: PacketWriter) {
        pw.write(ENCODE_EQUIP)
        encodeBase(pw)
        pw.writeByte(ruc)
        pw.writeByte(cuc)

        pw.writeShort(str)
        pw.writeShort(dex)
        pw.writeShort(int)
        pw.writeShort(luk)
        pw.writeShort(maxHP)
        pw.writeShort(maxMP)
        pw.writeShort(pad)
        pw.writeShort(mad)
        pw.writeShort(pdd)
        pw.writeShort(mdd)
        pw.writeShort(acc)
        pw.writeShort(eva)

        pw.writeShort(craft)
        pw.writeShort(speed)
        pw.writeShort(jump)
        pw.writeMapleString(title)
        pw.writeShort(attribute)

        pw.writeByte(levelUpType)
        pw.writeByte(level)
        pw.writeInt(exp)
        pw.writeInt(durability)

        pw.writeInt(iuc)

        pw.writeByte(grade)
        pw.writeByte(chuc)

        pw.writeShort(option1)
        pw.writeShort(option2)
        pw.writeShort(option3)
        pw.writeShort(0) // socket1
        pw.writeShort(0) // socket2

        if (cashItemSN == 0L) pw.writeLong(0)
        pw.writeLong(0)
        pw.writeInt(0)
    }

    private fun ItemSlotBundle.encode(pw: PacketWriter) {
        pw.write(ENCODE_BUNDLE)
        encodeBase(pw)

        pw.writeShort(number)
        pw.writeMapleString(title)
        pw.writeShort(attribute)

        if (isRechargeableItem(templateId)) {
            pw.writeLong(0)
        }
    }

    private fun ItemSlotPet.encode(pw: PacketWriter) {
        pw.write(ENCODE_PET)
        encodeBase(pw)

        pw.writeFixedString(petName, 13)
        pw.writeByte(level)
        pw.writeShort(tameness)
        pw.writeByte(repleteness)
        pw.writeLong(dateDead)

        pw.writeShort(petAttribute)
        pw.writeShort(petSkill)
        pw.writeInt(remainLife)
        pw.writeShort(attribute)
    }
}