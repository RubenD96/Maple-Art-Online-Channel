package net.maple.handlers.user

import client.Client
import client.player.StatType
import net.maple.handlers.PacketHandler
import util.packet.PacketReader

class UserChangeStatRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        reader.readInteger() // timestamp
        val flag = reader.readInteger()

        val hp = if (flag and StatType.HP.stat == StatType.HP.stat) reader.readShort().toInt() else 0
        val mp = if (flag and StatType.MP.stat == StatType.MP.stat) reader.readShort().toInt() else 0

        if (hp > 0 || mp > 0) {
            chr.modifyHPMP(hp, mp)
        }
    }
}