package net.maple.handlers.user

import client.Client
import client.player.StatType
import net.maple.handlers.PacketHandler
import util.packet.PacketReader
import java.util.*

class UserAbilityUpRequestHandler : PacketHandler() {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        reader.readInteger() // ?
        val type = reader.readInteger()

        if (chr.ap > 0) {
            chr.ap--
            val statTypes: MutableList<StatType> = ArrayList()
            statTypes.add(StatType.AP)

            when (type) {
                StatType.STR.stat -> {
                    chr.strength++
                    statTypes.add(StatType.STR)
                }
                StatType.DEX.stat -> {
                    chr.dexterity++
                    statTypes.add(StatType.DEX)
                }
                StatType.INT.stat -> {
                    chr.intelligence++
                    statTypes.add(StatType.INT)
                }
                StatType.LUK.stat -> {
                    chr.luck++
                    statTypes.add(StatType.LUK)
                }
            }

            chr.updateStats(statTypes, true)
        }
    }
}