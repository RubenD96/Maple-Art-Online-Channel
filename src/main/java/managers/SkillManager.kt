package managers

import skill.SkillTemplate
import util.logging.LogType
import util.logging.Logger

object SkillManager : Loadable {

    private val skills: MutableMap<Int, SkillTemplate> = HashMap()

    fun getSkill(id: Int): SkillTemplate? {
        synchronized(skills) {
            return skills[id] ?: run {
                val skill = SkillTemplate(id)
                if (!loadSkillData(skill)) {
                    Logger.log(LogType.MISSING, "Skill $id does not exist", this)
                    return null
                }
                skills[id] = skill
                skill
            }
        }
    }

    private fun loadSkillData(skill: SkillTemplate): Boolean {
        val r = getData("wz/Skill/" + skill.id + ".mao") ?: return false

        // todo load data

        return true
    }
}