package net.maple.handlers.user

import client.Client
import client.inventory.ItemInventoryType
import client.inventory.item.slots.ItemSlotBundle
import client.inventory.item.slots.ItemSlotEquip
import client.player.Job
import constants.ItemConstants.isTreatSingly
import field.obj.drop.ItemDrop
import net.database.ItemAPI
import net.maple.handlers.PacketHandler
import net.maple.packets.CharacterPackets.modifyInventory
import util.packet.PacketReader

class UserChangeSlotPositionRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        reader.readInteger()

        val type = ItemInventoryType.values()[reader.readByte() - 1]
        val from = reader.readShort()
        val to = reader.readShort()
        val number = reader.readShort()

        if (to.toInt() == 0) { // drop
            chr.modifyInventory({
                val inventory = chr.getInventory(type)
                var item = inventory.items[from] ?: return@modifyInventory
                item.updated = true
                val uuid = item.uuid

                if (from.toInt() == -11) {
                    chr.job = Job.WEAPONLESS
                }

                if (!isTreatSingly(item.templateId)) {
                    if (item !is ItemSlotBundle) return@modifyInventory
                    if (item.number < number) return@modifyInventory

                    item = it.getInventoryContext(type).take(from, number)
                    item.uuid = uuid

                    uuid?.run { ItemAPI.deleteItemByUUID(this) } // clear db entry
                } else {
                    it.getInventoryContext(type).remove(item)
                }

                val drop = ItemDrop(chr.id, chr, item, 0)
                drop.field = chr.field
                drop.position = chr.position
                drop.expire = System.currentTimeMillis() + 600000
                chr.field.enter(drop)
            }, true)
        } else {
            if (to.toInt() == -11) {
                val inventory = chr.getInventory(type)
                val item = inventory.items[from] as? ItemSlotEquip ?: return

                val job = when (item.templateId / 10000) {
                    130 -> Job.SWORD_1H4
                    133 -> Job.DAGGER4
                    140 -> Job.SWORD_2H4
                    143 -> Job.SPEAR4
                    148 -> Job.KNUCKLE4
                    else -> Job.WEAPONLESS
                }

                if (job != chr.job) chr.job = job
            }

            chr.modifyInventory({ it.getInventoryContext(type).move(from, to) }, true)
        }
    }
}