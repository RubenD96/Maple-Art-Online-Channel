package net.maple.handlers.user

import client.Client
import net.maple.handlers.PacketHandler
import skill.Macro
import util.packet.PacketReader

class UserMacroSysDataModifiedHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val macro = reader.readByte()
        if (macro > 5) return

        val macros = ArrayList<Macro>()
        repeat(macro.toInt()) {
            val name = reader.readMapleString()
            if (name.length > 13) return

            val shout = reader.readBool()
            val skill1 = reader.readInteger()
            val skill2 = reader.readInteger()
            val skill3 = reader.readInteger()

            macros.add(Macro(
                name,
                shout,
                intArrayOf(skill1, skill2, skill3)
            ))
        }

        val chr = c.character
        chr.macros[chr.job.type] = macros
    }
}