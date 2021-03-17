package net.maple.handlers.user

import client.Client
import client.inventory.ItemInventoryType
import client.inventory.item.slots.ItemSlotBundle
import client.inventory.item.slots.ItemSlotEquip
import client.inventory.item.templates.UpgradeScrollItemTemplate
import managers.ItemManager
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import net.maple.packets.CharacterPackets.modifyInventory
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter

class UserUpgradeItemUseRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        val timestamp = reader.readInteger()

        val scrollSlot = reader.readShort()
        val scroll = chr.getInventory(ItemInventoryType.CONSUME).items[scrollSlot]
            ?: return c.close(this, "Invalid scroll slot $scrollSlot")

        val template = ItemManager.getItem(scroll.templateId)
        if (template !is UpgradeScrollItemTemplate) return c.close(this, "Invalid itemid ${scroll.templateId}")

        val success = Math.random() < (template.success.toDouble() / 100)
        var cursed = false
        if (template.cursed != 0.toByte()) {
            cursed = Math.random() < (template.cursed.toDouble() / 100)
        }

        val equipSlot = reader.readShort()
        val equip = chr.getInventory(ItemInventoryType.EQUIP).items[equipSlot]
            ?: return c.close(this, "Invalid equip slot $equipSlot")

        if (equip is ItemSlotEquip) {
            if (equip.cuc < 1) return chr.enableActions()

            val whiteScroll = reader.readShort() == 2.toShort()
            if (whiteScroll && chr.getItemQuantity(2340000) < 1) return c.close(this, "Not enough white scrolls")

            val enchantSkill = reader.readBool()

            chr.modifyInventory({ it.take(scroll as ItemSlotBundle, 1) }, true)

            if (success) {
                equip.applyScroll(template)
            } else if (!whiteScroll) {
                equip.ruc--
            }

            if (!cursed) {
                chr.modifyInventory({ it.update(equip) })
            } else {
                chr.modifyInventory({ it.remove(equip) })
            }

            chr.field.broadcast(
                getShowItemUpgradeEffectPacket(
                    cid = chr.id,
                    success = success,
                    cursed = cursed,
                    whiteScroll = whiteScroll
                )
            )
        }
    }

    companion object {

        fun getShowItemUpgradeEffectPacket(
            cid: Int,
            success: Boolean,
            cursed: Boolean,
            enchantSkill: Boolean = false,
            enchantCategory: Int = 0,
            whiteScroll: Boolean,
            v6: Byte = 0
        ): Packet {
            val pw = PacketWriter(15)

            pw.writeHeader(SendOpcode.USER_ITEM_UPGRADE_EFFECT)

            pw.writeInt(cid)
            pw.writeBool(success)
            pw.writeBool(cursed)

            pw.writeBool(enchantSkill)
            pw.writeInt(enchantCategory)

            pw.writeBool(whiteScroll)
            pw.writeByte(v6)

            return pw.createPacket()
        }
    }
}