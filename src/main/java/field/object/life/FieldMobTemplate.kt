package field.`object`.life

import field.`object`.drop.DropEntry

data class FieldMobTemplate(val id: Int) {

    lateinit var name: String
    var moveType: MoveAbilityType = MoveAbilityType.JUMP
    var drops: MutableList<DropEntry>? = null
    var level: Short = 0
    var exp = 0
    var maxHP = 0
    var maxMP = 0
    var isBoss = false

    override fun toString(): String {
        return "FieldMobTemplate{id=$id, name='$name', moveType=$moveType, level=$level, exp=$exp, maxHP=$maxHP, maxMP=$maxMP, boss=$isBoss}"
    }
}