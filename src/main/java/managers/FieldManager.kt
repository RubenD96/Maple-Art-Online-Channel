package managers

import field.Field
import field.FieldTemplate
import field.obj.Foothold
import field.obj.life.AbstractFieldControlledLife
import field.obj.life.FieldLifeSpawnPoint
import field.obj.life.FieldMob
import field.obj.life.FieldNPC
import field.obj.portal.FieldPortal
import field.obj.reactor.FieldReactor
import field.obj.reactor.FieldReactorSpawnPoint
import managers.flag.FieldFlag
import util.logging.LogType
import util.logging.Logger.log
import java.awt.Point
import java.awt.Rectangle

class FieldManager : Loadable {

    companion object : Loadable {
        private const val fallback = 1000

        init {
            // assertion test to check if the fallback map (1000, town of beginnings) exists
            getData("wz/Map/$fallback.mao")!!
        }

        private val instanced = arrayOf(
                1999, 2999, 10999, 13999, 15999, // boss maps
                11121, // balloon
                11998, 11999, 12103, // boat ride to aqua
                10021, 10022, 10023, // 3rd job
                13001, 13201 // boat ride joel
        )

        fun isInstanced(id: Int): Boolean {
            if (instanced.contains(id)) return true
            if (id >= 101000) return true // floor 101
            if (id in 1602..1607) return true // DTPQ
            if (id in 12011..12015) return true // APQ
            if (id in 14402..14441) return true // CWPQ
            return false
        }
    }

    val templates: MutableMap<Int, FieldTemplate> = HashMap()
    val fields: MutableMap<Int, Field> = HashMap()

    fun getField(id: Int): Field {
        if (!isInstanced(id)) {
            synchronized(fields) {
                return fields[id]
                        ?: return loadFieldTemplateData(id)?.let { template ->
                            loadFieldData(template).also {
                                fields[id] = it
                            }
                        } ?: run {
                            log(LogType.MISSING, "Field $id does not exist", this)
                            getField(fallback)
                        }
            }
        } else {
            return loadFieldTemplateData(id)?.let {
                loadFieldData(it)
            } ?: run {
                log(LogType.MISSING, "Field $id does not exist", this)
                getField(fallback)
            }
        }
    }

    fun reloadField(id: Int) {
        synchronized(fields) {
            fields.remove(id)
        }
    }

    private fun loadFieldTemplateData(fieldId: Int): FieldTemplate? {
        synchronized(templates) {
            if (templates.contains(fieldId)) {
                return templates[fieldId]
            }

            val r = getData("wz/Map/$fieldId.mao") ?: return null

            val flags = r.readInteger()
            if (containsFlag(flags, FieldFlag.ID)) r.readInteger()

            var returnMap = 0
            if (containsFlag(flags, FieldFlag.RETURN_MAP)) returnMap = r.readInteger()

            var mapArea = Rectangle()
            if (containsFlag(flags, FieldFlag.MAP_AREA)) mapArea = r.readRectangle()

            val footholds = HashMap<Int, Foothold>()
            if (containsFlag(flags, FieldFlag.FOOTHOLDS)) {
                val size = r.readShort()
                repeat(size.toInt()) {
                    val fh = Foothold()
                    fh.decode(r)
                    footholds[fh.id] = fh
                }
            }

            val forcedReturnMap = if (containsFlag(flags, FieldFlag.FORCED_RETURN)) r.readInteger() else fieldId

            var fieldLimit = 0
            if (containsFlag(flags, FieldFlag.FIELD_LIMIT)) fieldLimit = r.readInteger()

            var name = ""
            if (containsFlag(flags, FieldFlag.NAME)) name = r.readMapleString()

            var script = ""
            if (containsFlag(flags, FieldFlag.ON_ENTER)) script = r.readMapleString()

            val portals = HashMap<Byte, FieldPortal>()
            if (containsFlag(flags, FieldFlag.PORTALS)) {
                val size = r.readShort()
                repeat(size.toInt()) {
                    val portal = FieldPortal()
                    portal.generate(r)
                    portals[portal.id.toByte()] = portal
                }
            }

            val areas = HashSet<Rectangle>()
            if (containsFlag(flags, FieldFlag.AREAS)) {
                val size = r.readShort()
                repeat(size.toInt()) {
                    areas.add(r.readRectangle())
                }
            }

            val mobSpawnPoints = ArrayList<FieldLifeSpawnPoint>()
            val npcSpawnPoints = ArrayList<FieldLifeSpawnPoint>()
            if (containsFlag(flags, FieldFlag.LIFE)) {
                val size = r.readShort()
                repeat(size.toInt()) {
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
                        if (time == 0) time = 5
                        mobSpawnPoints.add(FieldLifeSpawnPoint(
                                id,
                                Point(x, y),
                                rx0,
                                rx1,
                                cy,
                                time,
                                fh.toShort(),
                                f == 0,
                                hide == 1
                        ))
                    } else { // npc
                        npcSpawnPoints.add(FieldLifeSpawnPoint(
                                id,
                                Point(x, y),
                                rx0,
                                rx1,
                                cy,
                                time,
                                fh.toShort(),
                                f == 0,
                                hide == 1
                        ))
                    }
                }
            }

            val reactorSpawnPoints = ArrayList<FieldReactorSpawnPoint>()
            if (containsFlag(flags, FieldFlag.REACTOR)) {
                val size = r.readShort()
                repeat(size.toInt()) {
                    val rid = r.readInteger()
                    val rposition = Point(r.readInteger(), r.readInteger())
                    val rtime = r.readInteger()
                    val rf = r.readBool()
                    val rname = r.readMapleString()

                    reactorSpawnPoints.add(FieldReactorSpawnPoint(rid, rposition, rtime, rf, rname))
                }
            }

            return FieldTemplate(
                    id = fieldId,
                    returnMap = returnMap,
                    mapArea = mapArea,
                    footholds = footholds,
                    forcedReturnMap = forcedReturnMap,
                    fieldLimit = fieldLimit,
                    name = name,
                    script = script,
                    portals = portals,
                    areas = areas,
                    mobSpawnPoints = mobSpawnPoints,
                    npcSpawnPoints = npcSpawnPoints,
                    reactorSpawnPoints = reactorSpawnPoints
            )
        }
    }

    private fun loadFieldData(template: FieldTemplate): Field {
        val field = Field(template)

        template.mobSpawnPoints.forEach {
            with(it) {
                val mobTemplate = MobManager.getMob(id)

                val mob = FieldMob(mobTemplate, f)
                mob.hp = mob.template.maxHP
                mob.mp = mob.template.maxMP
                mob.home = fh
                mob.time = time

                loadLifePosition(mob, this)
                mob.field = field
                field.enter(mob)
            }
        }

        template.npcSpawnPoints.forEach {
            with(it) {
                val npcTemplate = NPCManager.getNPC(id)
                val npc = FieldNPC(npcTemplate)

                loadLifePosition(npc, this)
                npc.field = field
                field.enter(npc)
            }
        }

        template.reactorSpawnPoints.forEach {
            with(it) {
                val reactorTemplate = ReactorManager.getReactor(id)

                val reactor = FieldReactor(reactorTemplate)
                reactor.position = position
                reactor.time = time
                reactor.f = f
                reactor.name = name

                reactor.field = field
                field.enter(reactor)
            }
        }

        return field

        /*val r = getData("wz/Map/" + field.id + ".mao") ?: return false

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
        return true*/
    }

    private fun loadLifePosition(life: AbstractFieldControlledLife, spawnPoint: FieldLifeSpawnPoint) {
        with(spawnPoint) {
            life.rx0 = rx0
            life.rx1 = rx1
            life.position = position
            life.foothold = fh
            life.f = f
            life.cy = cy
            life.hide = hide
        }
    }

    private fun containsFlag(flags: Int, flag: FieldFlag): Boolean {
        return flags and flag.value == flag.value
    }
}