package managers

import field.Field
import field.obj.Foothold
import field.obj.life.AbstractFieldControlledLife
import field.obj.life.FieldMob
import field.obj.life.FieldMobSpawnPoint
import field.obj.life.FieldNPC
import field.obj.portal.FieldPortal
import field.obj.reactor.FieldReactor
import managers.flag.FieldFlag
import util.logging.LogType
import util.logging.Logger.log
import java.awt.Point

class FieldManager : Loadable {

    // assertion test to check if the fallback map (1000, town of beginnings) exists
    companion object : Loadable {
        private const val fallback = 1000

        init {
            getData("wz/Map/$fallback.mao")!!
        }

        private val instanced = arrayOf(
                1999, 2999, 10999, 13999, // boss maps
                11121, // balloon
                11998, 11999, 12103, // boat ride to aqua
                10021, 10022, 10023, // 3rd job
                13001, 13201, // boat ride joel
                1602, 1603, 1604, 1605, 1606, 1607, // Deep Tree PQ
                12011, 12012, 12013, 12014, 12015 // Aqua PQ
        )
    }

    val fields: MutableMap<Int, Field> = HashMap()

    fun getField(id: Int): Field {
        if (!isInstanced(id)) {
            synchronized(fields) {
                var field = fields[id]

                if (field == null) {
                    field = Field(id)
                    if (!loadFieldData(field)) {
                        log(LogType.MISSING, "Field $id does not exist", this)
                        return getField(fallback)
                    }
                    fields[id] = field
                }

                return field
            }
        } else {
            val field = Field(id)

            if (!loadFieldData(field)) {
                log(LogType.MISSING, "Field $id does not exist", this)
                return getField(fallback)
            }

            return field
        }
    }

    fun reloadField(id: Int) {
        synchronized(fields) {
            fields.remove(id)
        }
    }

    private fun isInstanced(id: Int): Boolean {
        if (instanced.contains(id)) return true
        if (id >= 101000) return true // floor 101
        if (id in 14402..14441) return true // CWPQ, just too many maps to do manually
        return false
    }

    private fun loadFieldData(field: Field): Boolean {
        val r = getData("wz/Map/" + field.id + ".mao") ?: return false

        val flags = r.readInteger()
        if (containsFlag(flags, FieldFlag.ID)) r.readInteger()
        if (containsFlag(flags, FieldFlag.RETURN_MAP)) field.returnMap = r.readInteger()
        if (containsFlag(flags, FieldFlag.MAP_AREA)) field.mapArea = r.readRectangle()

        if (containsFlag(flags, FieldFlag.FOOTHOLDS)) {
            val size = r.readShort()
            repeat(size.toInt()) {
                val fh = Foothold()
                fh.decode(r)
                field.footholds[fh.id] = fh
            }
        }

        field.forcedReturnMap = if (containsFlag(flags, FieldFlag.FORCED_RETURN)) r.readInteger() else field.id
        if (containsFlag(flags, FieldFlag.FIELD_LIMIT)) field.fieldLimit = r.readInteger()
        if (containsFlag(flags, FieldFlag.NAME)) field.name = r.readMapleString()
        if (containsFlag(flags, FieldFlag.ON_ENTER)) field.script = r.readMapleString()

        if (containsFlag(flags, FieldFlag.PORTALS)) {
            val size = r.readShort()
            repeat(size.toInt()) {
                val portal = FieldPortal(field)
                portal.generate(r)
                field.portals[portal.id.toByte()] = portal
            }
        }

        if (containsFlag(flags, FieldFlag.AREAS)) {
            val size = r.readShort()
            repeat(size.toInt()) {
                r.readRectangle()
            }
        }

        if (containsFlag(flags, FieldFlag.LIFE)) {
            val size = r.readShort()
            repeat(size.toInt()) {
                val life: AbstractFieldControlledLife
                val id = r.readInteger()
                var time = r.readInteger()
                val x = r.readInteger()
                val y = r.readInteger()
                val f = r.readInteger()
                val hide = r.readInteger()
                val fh = r.readInteger()
                val cy = r.readInteger()
                val rx0 = r.readInteger()
                val rx1 = r.readInteger()
                val type = r.readMapleString()

                if (type == "m") {
                    val template = MobManager.getMob(id)

                    val mob = FieldMob(template, f == 1)
                    mob.hp = mob.template.maxHP
                    mob.mp = mob.template.maxMP
                    mob.home = fh.toShort()
                    if (time == 0) time = 5
                    mob.time = time
                    life = mob

                    field.mobSpawnPoints.add(FieldMobSpawnPoint(id, Point(x, y), rx0, rx1, cy, time, fh.toShort()))
                } else { // npc
                    val template = NPCManager.getNPC(id)
                    val npc = FieldNPC(template)
                    life = npc
                }

                life.rx0 = rx0
                life.rx1 = rx1
                life.position = Point(x, y)
                life.foothold = fh.toShort()
                life.f = f == 0
                life.cy = cy
                life.hide = hide == 1
                life.field = field
                field.enter(life)
            }
        }

        if (containsFlag(flags, FieldFlag.REACTOR)) {
            val size = r.readShort()
            repeat(size.toInt()) {
                val template = ReactorManager.getReactor(r.readInteger())

                val reactor = FieldReactor(template)
                reactor.decode(r)
                reactor.field = field

                field.enter(reactor)
            }
        }

        //System.out.println("Finished initializing field: " + field.getId());
        return true
    }

    private fun containsFlag(flags: Int, flag: FieldFlag): Boolean {
        return flags and flag.value == flag.value
    }
}