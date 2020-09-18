package net.maple.handlers.user

import client.Client
import client.player.StatType
import net.maple.handlers.PacketHandler
import util.packet.PacketReader
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

class UserAbilityMassUpRequestHandler : PacketHandler() {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        reader.readInteger() // ?
        val count = reader.readInteger()

        val inc: MutableMap<Int, Int> = HashMap()
        var total = 0
        for (i in 0 until count) {
            val type = reader.readInteger()
            val amount = reader.readInteger()
            inc[type] = amount
            total += amount
        }

        if (chr.ap < total) {
            c.close(this, "More stats than AP available")
            return
        }

        val statTypes: MutableList<StatType> = ArrayList()
        statTypes.add(StatType.AP)
        chr.ap = chr.ap - total
        inc.forEach {
            when (it.key) {
                StatType.STR.stat -> {
                    chr.strength = chr.strength + it.value
                    statTypes.add(StatType.STR)
                }
                StatType.DEX.stat -> {
                    chr.dexterity = chr.dexterity + it.value
                    statTypes.add(StatType.DEX)
                }
                StatType.INT.stat -> {
                    chr.intelligence = chr.intelligence + it.value
                    statTypes.add(StatType.INT)
                }
                StatType.LUK.stat -> {
                    chr.luck = chr.luck + it.value
                    statTypes.add(StatType.LUK)
                }
            }
        }

        chr.updateStats(statTypes, true)
    }
}