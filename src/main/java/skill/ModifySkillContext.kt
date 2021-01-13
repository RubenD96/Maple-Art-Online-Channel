package skill

import client.Character
import client.player.Skill
import constants.PacketConstants
import util.packet.PacketWriter

class ModifySkillContext(private val character: Character) {

    private val skills: MutableMap<Int, Skill> = HashMap()

    fun add(skillId: Int, masterLevel: Int = 0, expire: Long = 0) {
        character.skills[skillId]?.let {
            set(skillId, it.level + 1, masterLevel, expire)
        } ?: run {
            set(
                skillId = skillId,
                masterLevel = masterLevel,
                expire = expire
            )
        }
    }

    fun set(skillId: Int, level: Int = 1, masterLevel: Int = 0, expire: Long = 0) {
        character.skills[skillId]?.let {
            if (level <= 0) character.skills.remove(skillId)

            it.level = level
            it.masterLevel = masterLevel
            it.expire = expire
            skills[skillId] = it
        } ?: run {
            val skill = Skill(level)
            skill.masterLevel = masterLevel
            skill.expire = expire

            character.skills[skillId] = skill
            skills[skillId] = skill
        }
    }

    fun encode(pw: PacketWriter) {
        pw.writeShort(skills.size)
        skills.forEach {
            pw.writeInt(it.key) // id
            pw.writeInt(it.value.level)
            pw.writeInt(it.value.masterLevel)
            pw.writeLong(if (it.value.expire > 0) it.value.expire else PacketConstants.permanent) // permanent
        }
    }
}