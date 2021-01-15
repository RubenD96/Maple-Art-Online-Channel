package net.maple.handlers.user

import client.Client
import client.player.StatType
import net.maple.handlers.PacketHandler
import util.packet.PacketReader
import java.util.*

class UserAbilityUpRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        reader.readInteger() // timestamp
        val type = reader.readInteger()

        if (chr.ap > 0) {
            chr.ap--

            when (type) {
                StatType.STR.stat -> chr.strength++
                StatType.DEX.stat -> chr.dexterity++
                StatType.INT.stat -> chr.intelligence++
                StatType.LUK.stat -> chr.luck++
            }
        }
    }
}