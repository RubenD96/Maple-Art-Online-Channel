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

        var hp = 0
        var mp = 0

        if (flag and StatType.HP.stat == StatType.HP.stat) {
            hp = reader.readShort().toInt()
        }

        if (flag and StatType.MP.stat == StatType.MP.stat) {
            mp = reader.readShort().toInt()
        }

        if (hp > 0 || mp > 0) {
            chr.modifyHPMP(hp, mp)
        }
    }
}