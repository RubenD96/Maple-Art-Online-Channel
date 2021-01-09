package net.maple.handlers.item

import client.Client
import client.inventory.item.slots.ItemSlot
import client.inventory.item.templates.StatChangeItemTemplate
import client.stats.TemporaryStatExtensions.getTemporaryStats
import client.stats.TemporaryStatType
import net.maple.packets.CharacterPackets.modifyInventory
import net.maple.packets.CharacterPackets.modifyTemporaryStats
import util.packet.PacketReader

class UserStatChangeItemUseRequestHandler : AbstractItemUseHandler<StatChangeItemTemplate>() {

    override fun handlePacket(reader: PacketReader, c: Client, template: StatChangeItemTemplate, item: ItemSlot) {
        val stats = template.getTemporaryStats()

        if (stats.isNotEmpty()) {
            c.character.modifyTemporaryStats {
                if (template.time > 0) {
                    val expire = System.currentTimeMillis() + template.time * 1000
                    stats.forEach { ts ->
                        it.set(ts.key, ts.value.toInt(), -template.id, expire)
                    }
                } else {
                    stats.forEach { ts ->
                        it.set(ts.key, ts.value.toInt(), -template.id)
                    }
                }
            }
        }

        if (!stats.containsKey(TemporaryStatType.MORPH)) {
            var incHP = 0
            var incMP = 0

            incHP += template.HP
            incMP += template.MP
            incHP += c.character.maxHealth * (template.HPR / 100)
            incMP += c.character.maxMana * (template.MPR / 100)

            if (incHP > 0 || incMP > 0) {
                c.character.modifyHPMP(incHP, incMP)
            }
        }

        c.character.modifyInventory({ it.remove(item, 1) }, true)
    }
}