package net.maple.handlers.user

import client.Client
import client.effects.user.SkillUseEffect
import client.stats.TemporaryStatExtensions.getTemporaryStats
import managers.SkillManager
import net.maple.handlers.PacketHandler
import net.maple.packets.CharacterPackets.localEffect
import net.maple.packets.CharacterPackets.modifyTemporaryStats
import net.maple.packets.CharacterPackets.remoteEffect
import net.maple.packets.CharacterPackets.statUpdate
import util.packet.PacketReader

class UserSkillUseRequestHandler: PacketHandler {

    // 01 00 00
    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        val timestamp = reader.readInteger()
        val id = reader.readInteger()
        val template = SkillManager.getSkill(id) ?: return run { c.close(this, "Invalid skillid use") }

        val skillLevel = chr.skills[id]?.level ?: return run { c.close(this, "Skill not leveled") }
        val data = template.levelData[skillLevel] ?: return run { c.close(this, "level data does not exist") }
        val stats = data.getTemporaryStats()

        if (stats.isNotEmpty()) {
            chr.modifyTemporaryStats {
                if (data.time > 0) {
                    val expire = System.currentTimeMillis() + (data.time * 1000)
                    stats.forEach { t -> it.set(t.key, id, t.value.toInt(), expire) }
                } else {
                    stats.forEach { t -> it.set(t.key, id, t.value.toInt()) }
                }
            }
        }

        chr.remoteEffect(SkillUseEffect(id, skillLevel.toByte()))
    }
}