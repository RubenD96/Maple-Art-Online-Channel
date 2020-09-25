package managers

import field.obj.life.FieldMobTemplate
import util.logging.LogType
import util.logging.Logger
import util.logging.Logger.log

object MobManager : AbstractManager() {

    private const val fallback = 100100

    // assertion test to check if the fallback mob (100100, snail) exists
    init {
        getData("wz/Mob/$fallback.mao")!!
    }

    private val mobs: MutableMap<Int, FieldMobTemplate> = HashMap()

    fun getMob(id: Int): FieldMobTemplate {
        synchronized(mobs) {
            var mob = mobs[id]

            if (mob == null) {
                mob = FieldMobTemplate(id)
                if (!loadMobData(mob)) {
                    log(LogType.MISSING, "mob $id does not exist", this)
                    return getMob(fallback)
                }
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