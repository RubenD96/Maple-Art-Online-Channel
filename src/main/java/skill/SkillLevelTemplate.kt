package skill

import util.packet.PacketReader

class SkillLevelTemplate(val id: Int, val skill: Int, val flags: Long) {

    var hp: Short = 0
    var mp: Short = 0
    var pad: Short = 0
    var pdd: Short = 0
    var mad: Short = 0
    var mdd: Short = 0
    var acc: Short = 0
    var eva: Short = 0
    var craft: Short = 0
    var speed: Short = 0
    var jump: Short = 0

    var morph: Short = 0
    var hpCon: Short = 0
    var mpCon: Short = 0
    var moneyCon: Short = 0
    var itemCon: Short = 0
    var itemConNo: Short = 0
    var Damage: Short = 0

    var fixDamage: Short = 0
    var selfDestruction: Short = 0

    var time: Short = 0
    var subProp: Short = 0

    var attackCount: Short = 0
    var bulletCount: Short = 0
    var bulletConsume: Short = 0
    var mastery: Short = 0
    var mobCount: Short = 0

    var x: Short = 0
    var y: Short = 0
    var z: Short = 0

    var emhp: Short = 0
    var emmp: Short = 0
    var epad: Short = 0
    var emad: Short = 0
    var epdd: Short = 0
    var emdd: Short = 0

    fun loadSkillLevelTemplate(r: PacketReader) {
        if (containsFlag(SkillFlag.HP)) hp = r.readShort()
        if (containsFlag(SkillFlag.MP)) hp = r.readShort()
        if (containsFlag(SkillFlag.PAD)) hp = r.readShort()
        if (containsFlag(SkillFlag.PDD)) hp = r.readShort()
        if (containsFlag(SkillFlag.MAD)) hp = r.readShort()
        if (containsFlag(SkillFlag.MDD)) hp = r.readShort()
        if (containsFlag(SkillFlag.ACC)) hp = r.readShort()
        if (containsFlag(SkillFlag.EVA)) hp = r.readShort()
        if (containsFlag(SkillFlag.CRAFT)) hp = r.readShort()
        if (containsFlag(SkillFlag.SPEED)) hp = r.readShort()
        if (containsFlag(SkillFlag.JUMP)) hp = r.readShort()

        if (containsFlag(SkillFlag.MORPH)) hp = r.readShort()
        if (containsFlag(SkillFlag.HP_CON)) hp = r.readShort()
        if (containsFlag(SkillFlag.MP_CON)) hp = r.readShort()
        if (containsFlag(SkillFlag.MONEY_CON)) hp = r.readShort()
        if (containsFlag(SkillFlag.ITEM_CON)) hp = r.readShort()
        if (containsFlag(SkillFlag.ITEM_CON_NO)) hp = r.readShort()
        if (containsFlag(SkillFlag.DAMAGE)) hp = r.readShort()

        if (containsFlag(SkillFlag.FIX_DAMAGE)) hp = r.readShort()
        if (containsFlag(SkillFlag.SELF_DESTRUCTION)) hp = r.readShort()
        if (containsFlag(SkillFlag.TIME)) hp = r.readShort()
        if (containsFlag(SkillFlag.SUB_PROP)) hp = r.readShort()
        if (containsFlag(SkillFlag.ATTACK_COUNT)) hp = r.readShort()
        if (containsFlag(SkillFlag.BULLET_COUNT)) hp = r.readShort()
        if (containsFlag(SkillFlag.BULLET_CONSUME)) hp = r.readShort()
        if (containsFlag(SkillFlag.MASTERY)) hp = r.readShort()
        if (containsFlag(SkillFlag.MOB_COUNT)) hp = r.readShort()

        if (containsFlag(SkillFlag.X)) hp = r.readShort()
        if (containsFlag(SkillFlag.Y)) hp = r.readShort()
        if (containsFlag(SkillFlag.Z)) hp = r.readShort()

        if (containsFlag(SkillFlag.EMHP)) hp = r.readShort()
        if (containsFlag(SkillFlag.EMMP)) hp = r.readShort()
        if (containsFlag(SkillFlag.EPAD)) hp = r.readShort()
        if (containsFlag(SkillFlag.EMAD)) hp = r.readShort()
        if (containsFlag(SkillFlag.EPDD)) hp = r.readShort()
        if (containsFlag(SkillFlag.EMDD)) hp = r.readShort()
    }

    private fun containsFlag(flag: SkillFlag): Boolean {
        return flags and flag.value == flag.value
    }
}