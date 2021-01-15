package net.maple.handlers.user

import client.Client
import client.player.StatType
import net.maple.handlers.PacketHandler
import util.packet.PacketReader
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

class UserAbilityMassUpRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        reader.readInteger() // timestamp
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

        chr.ap -= total
        inc.forEach {
            when (it.key) {
                StatType.STR.stat -> chr.strength += it.value
                StatType.DEX.stat -> chr.dexterity += it.value
                StatType.INT.stat -> chr.intelligence += it.value
                StatType.LUK.stat -> chr.luck += it.value
            }
        }
    }
}