package net.maple.handlers.user

import client.Client
import managers.SkillManager
import net.maple.handlers.PacketHandler
import util.packet.PacketReader

class UserSkillUseRequestHandler: PacketHandler {

    // 01 00 00
    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        val timestamp = reader.readInteger()
        val id = reader.readInteger()
        val template = SkillManager.getSkill(id) ?: return run { c.close(this, "Invalid skillid use") }

        chr.skills[id]?.let {

        }
    }
}