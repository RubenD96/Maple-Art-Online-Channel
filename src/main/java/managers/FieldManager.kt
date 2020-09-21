package managers

import field.Field
import field.obj.Foothold
import field.obj.life.AbstractFieldControlledLife
import field.obj.life.FieldMob
import field.obj.life.FieldMobSpawnPoint
import field.obj.life.FieldNPC
import field.obj.portal.FieldPortal
import managers.flag.FieldFlag
import java.awt.Point

class FieldManager : AbstractManager() {

    val fields: MutableMap<Int, Field> = HashMap()
    var secureToB: Field = getField(1000)!!

    fun getField(id: Int): Field? {
        synchronized(fields) {
            var field = fields[id]

            if (field == null) {
                field = Field(id)
                if (!loadFieldData(field)) {
                    System.err.println("Field " + field.id + " does not exist!")
                    return null
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
            for (i in 0 until size) {
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
            for (i in 0 until size) {
                val portal = FieldPortal(field)
                portal.generate(r)
                field.portals[portal.id.toByte()] = portal
            }
        }

        if (containsFlag(flags, FieldFlag.AREAS)) {
            val size = r.readShort()
            for (i in 0 until size) {
                r.readRectangle()
            }
        }

        if (containsFlag(flags, FieldFlag.LIFE)) {
            val size = r.readShort()
            for (i in 0 until size) {
                var obj: AbstractFieldControlledLife
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
                    val template = MobManager.getMob(id) ?: continue // mob data doesn't exist

                    val mob = FieldMob(template, f == 1)
                    mob.hp = mob.template.maxHP
                    mob.mp = mob.template.maxMP
                    mob.home = fh.toShort()
                    if (time == 0) time = 5
                    mob.time = time
                    obj = mob

                    field.mobSpawnPoints.add(FieldMobSpawnPoint(id, Point(x, y), rx0, rx1, cy, time, fh.toShort()))
                } else { // npc
                    val template = NPCManager.getNPC(id) ?: continue
                    val npc = FieldNPC(template)
                    obj = npc
                }

                obj.rx0 = rx0
                obj.rx1 = rx1
                obj.position = Point(x, y)
                obj.foothold = fh.toShort()
                obj.f = f == 0
                obj.cy = cy
                obj.hide = hide == 1
                obj.field = field
                field.enter(obj)
            }
        }
        //System.out.println("Finished initializing field: " + field.getId());
        return true
    }

    private fun containsFlag(flags: Int, flag: FieldFlag): Boolean {
        return flags and flag.value == flag.value
    }
}