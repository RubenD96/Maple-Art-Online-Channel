package net.maple.handlers.user

import client.Client
import managers.SkillManager
import net.maple.handlers.PacketHandler
import net.maple.packets.CharacterPackets.modifySkills
import skill.SkillTemplate
import util.logging.LogType
import util.logging.Logger
import util.packet.PacketReader

class UserSkillUpRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        val timestamp = reader.readInteger()
        val id = reader.readInteger()
        val template = SkillManager.getSkill(id) ?: return run { c.close(this, "Invalid skillid levelup") }

        template.maxLevel = 30

        if (chr.sp <= 0) return

        // return if skill exists and is above or equal to max level
        chr.skills[id]?.let {
            if (it.level >= template.maxLevel) {
                chr.enableActions()
                Logger.log(LogType.MISC_CONSOLE, "Attempt at leveling past max level: max=${template.maxLevel} id=$id", this, c)
                // don't kick, might be cause of lag
                return
            }
        }/* ?: run {
            chr.modifySkills { it.add(id) } // thonk kotlin magic
            return
        }*/

        chr.sp--
        chr.modifySkills { it.add(id) }
    }
}