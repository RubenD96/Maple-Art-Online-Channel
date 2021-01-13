package skill

class SkillTemplate(val id: Int) {

    var maxLevel: Short = 0
    var summon: Boolean = false

    val reqSkill: Map<Int, Int> = HashMap()
    val levelData: Map<Int, SkillLevelTemplate> = HashMap()

    init {

    }
}