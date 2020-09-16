package managers

import field.obj.life.FieldMobTemplate

object MobManager : AbstractManager() {

    private val mobs: MutableMap<Int, FieldMobTemplate> = HashMap()

    fun getMob(id: Int): FieldMobTemplate? {
        synchronized(mobs) {
            var mob = mobs[id]

            if (mob == null) {
                mob = FieldMobTemplate(id)
                if (!loadMobData(mob)) return null
                mobs[id] = mob
            }

            return mob
        }
    }

    private fun loadMobData(mob: FieldMobTemplate): Boolean {
        val r = getData("wz/Mob/" + mob.id + ".mao") ?: return false

        mob.level = r.readShort()
        mob.name = r.readMapleString()
        mob.exp = r.readInteger()
        mob.maxHP = r.readInteger()
        mob.maxMP = r.readInteger()
        mob.isBoss = r.readBool()

        return true
    }
}