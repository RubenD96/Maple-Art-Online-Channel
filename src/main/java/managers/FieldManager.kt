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
import java.awt.Point

class FieldManager : AbstractManager() {

    // assertion test to check if the fallback map (1000, town of beginnings) exists
    companion object {
        private const val fallback = 1000

        init {
            getData("wz/Map/$fallback.mao")!!
        }
    }

    val fields: MutableMap<Int, Field> = HashMap()

    fun getField(id: Int): Field {
        synchronized(fields) {
            var field = fields[id]

            if (field == null) {
                field = Field(id)
                if (!loadFieldData(field)) {
                    System.err.println("Field $id does not exist!")
                    return getField(fallback)
                }
                fields[id] = field
            }

            return field
        }
    }

    fun reloadField(id: Int) {
        synchronized(fields) {
            fields.remove(id)
        }
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
                val reactor = FieldReactor(r.readInteger())
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