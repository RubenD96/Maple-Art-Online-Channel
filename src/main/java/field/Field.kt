package field

import client.Character
import client.player.quest.QuestState
import client.replay.MoveCollection
import client.replay.Replay
import constants.FieldConstants.JQ_FIELDS
import field.obj.FieldObject
import field.obj.FieldObjectType
import field.obj.Foothold
import field.obj.drop.AbstractFieldDrop
import field.obj.drop.EnterType
import field.obj.life.*
import field.obj.portal.FieldPortal
import field.obj.portal.PortalType
import managers.MobManager
import net.maple.packets.FieldPackets.setField
import net.maple.packets.PartyPackets.updateParty
import net.server.Server.getCharacter
import scripting.field.FieldScriptManager.execute
import util.logging.LogType
import util.logging.Logger
import util.packet.Packet
import java.awt.Point
import java.awt.Rectangle
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashSet

class Field(val id: Int) {

    var returnMap = 0
    var forcedReturnMap = 0
    var fieldLimit = 0
    lateinit var name: String
    lateinit var script: String
    lateinit var mapArea: Rectangle

    var replay: Replay? = null

    val portals: MutableMap<Byte, FieldPortal> = HashMap()
    val footholds: MutableMap<Int, Foothold> = HashMap()
    private val objects: MutableMap<FieldObjectType, MutableSet<FieldObject>> = EnumMap(FieldObjectType::class.java)
    val mobSpawnPoints: MutableList<FieldMobSpawnPoint> = ArrayList<FieldMobSpawnPoint>()
    private val toRespawn: MutableList<Respawn> = ArrayList()

    companion object {
        private val runningObjectId = AtomicInteger(1000000000)
    }

    fun startReplay() {
        replay?.stop()
        replay = Replay().load(this)?.also { enter(it) }
    }

    init {
        for (type in FieldObjectType.values()) {
            objects[type] = LinkedHashSet()
        }

        if (JQ_FIELDS.contains(id)) {
            startReplay()
        }
    }

    fun broadcast(packet: Packet, source: Character? = null) {
        getObjects(FieldObjectType.CHARACTER).stream()
                .filter { it !== source }
                .forEach { (it as? Character)?.write(packet.clone()) }
    }

    fun enter(chr: Character, portalName: String) {
        val portal = getPortalByName(portalName) ?: return
        enter(chr, portal.id.toByte())
    }

    fun enter(chr: Character, portal: Byte) {
        chr.portal = portal
        enter(chr)
    }

    fun enter(obj: FieldObject) {
        if (obj.field != this) {
            obj.field.leave(obj, obj.leaveFieldPacket)
            obj.field = this
        }

        addObject(obj)

        if (obj is Character) {
            val portal: FieldPortal = portals[obj.portal] ?: firstSpawnpoint
            obj.id = obj.id
            obj.fieldId = id
            obj.moveCollections[id] = MoveCollection(obj, id)
            obj.position = portal.position
            obj.foothold = (if (portal.type != PortalType.START_POINT) getFhByPortal(portal).id else 0).toShort()
            obj.write(obj.setField())
            broadcast(obj.enterFieldPacket, obj)

            synchronized(objects) {
                objects.values.forEach { set ->
                    set.stream()
                            .filter { it != obj }
                            .forEach {
                                when (it) {
                                    is AbstractFieldDrop -> enterItemDrop(it, it.enterFieldPacket)
                                    is FieldMob -> obj.write(it.getEnterFieldPacket(MobSummonType.NORMAL))
                                    else -> obj.write(it.enterFieldPacket)
                                }
                            }
                }
            }

            obj.party?.let { party ->
                val me = party.getMember(obj.id) ?: return@let
                me.field = id
                party.getMembers().forEach { member ->
                    if (member.isOnline && member.channel == obj.getChannel().channelId) {
                        val mem = getCharacter(member.cid) ?: return@forEach
                        mem.write(updateParty(party, member.channel))

                        if (member.field == id) {
                            mem.updatePartyHP(true)
                        }
                    }
                }
            }

            if (script.isNotEmpty()) { // never happens as we set script to mapid when onEnter is not present
                execute(obj.client, this, script)
            }
        } else {
            obj.id = runningObjectId.addAndGet(1)
            when (obj) {
                is AbstractFieldDrop -> enterItemDrop(obj, obj.getEnterFieldPacket(EnterType.PARTY))
                is Replay -> {
                    broadcast(obj.enterFieldPacket)
                    obj.start()
                }
                else -> broadcast(obj.enterFieldPacket)
            }
        }
        updateControlledObjects()
    }

    private fun enterItemDrop(drop: AbstractFieldDrop, enterPacket: Packet) {
        getObjects(FieldObjectType.CHARACTER).forEach {
            val chr = it as Character
            if (drop.questId != 0) {
                val quest = chr.quests[drop.questId]
                if (quest != null && quest.state !== QuestState.PERFORM) {
                    return@forEach
                }
            }
            chr.write(enterPacket.clone())
        }
    }

    fun leave(obj: FieldObject, leaveFieldPacket: Packet? = null) {
        removeObject(obj)
        when (obj) {
            is Character -> {
                broadcast(obj.leaveFieldPacket, obj)
            }
            is Replay -> {
                broadcast(obj.leaveFieldPacket)
                replay = null
            }
            else -> {
                broadcast(leaveFieldPacket!!)
            }
        }
        updateControlledObjects()
    }

    fun respawn() {
        synchronized(toRespawn) {
            val spawned: MutableList<Respawn> = ArrayList()
            val time = System.currentTimeMillis()
            toRespawn.forEach { respawn ->
                if (time > respawn.time) {
                    getRandomViableMobSpawnPoint(respawn.mob)?.let {
                        val template: FieldMobTemplate = MobManager.getMob(respawn.mob)
                        val mob = FieldMob(template, false)
                        mob.hp = mob.template.maxHP
                        mob.mp = mob.template.maxMP
                        mob.home = it.fh
                        mob.time = respawn.cooldown
                        mob.rx0 = it.rx0
                        mob.rx1 = it.rx1
                        mob.position = it.point
                        mob.foothold = it.fh
                        mob.cy = it.cy
                        mob.hide = false
                        mob.field = this
                        enter(mob)
                    } ?: Logger.log(LogType.MISC_CONSOLE, "No respawn location found for $respawn", this)
                    spawned.add(respawn)
                }
            }
            spawned.forEach { toRespawn.remove(it) }
        }
    }

    private fun getRandomViableMobSpawnPoint(mob: Int): FieldMobSpawnPoint? {
        if (mobSpawnPoints.isEmpty()) return null // spawned in a map with no mob spawns, don't respawn

        val spawnPoints = mobSpawnPoints.stream().filter { sp: FieldMobSpawnPoint -> sp.id == mob }.toArray()
        return spawnPoints[Random().nextInt(spawnPoints.size)] as FieldMobSpawnPoint
    }

    fun removeExpiredDrops() {
        val drops: MutableList<AbstractFieldDrop> = ArrayList<AbstractFieldDrop>()
        val time = System.currentTimeMillis()
        getObjects(FieldObjectType.DROP).forEach {
            val drop: AbstractFieldDrop = it as AbstractFieldDrop
            if (time > drop.expire) {
                drops.add(drop)
            }
        }
        drops.forEach { leave(it, it.leaveFieldPacket) }
    }

    @Synchronized
    fun updateControlledObjects() {
        val characters: List<FieldObject> = getObjects(FieldObjectType.CHARACTER).stream()
                .sorted(Comparator.comparingInt { (it as Character).controlledObjects.size })
                .collect(Collectors.toList())

        val types: Array<FieldObjectType> = arrayOf(FieldObjectType.NPC, FieldObjectType.MOB)
        val controlled: List<FieldObject> = getObjects(*types).stream()
                .filter { it is FieldControlledObject }
                .collect(Collectors.toList())

        controlled.stream()
                .filter {
                    val c: FieldControlledObject = it as FieldControlledObject
                    c.controller == null || !characters.contains(c.controller)
                }.forEach {
                    (it as FieldControlledObject).controller = characters.stream().findFirst().orElse(null) as Character?
                }
    }

    fun getControlledObject(chr: Character, oid: Int): FieldControlledObject? {
        val types: Array<FieldObjectType> = arrayOf(FieldObjectType.NPC, FieldObjectType.MOB)
        return getObjects(*types).stream().filter {
            (it as FieldControlledObject).controller == chr && it.id == oid
        }.findAny().orElse(null) as FieldControlledObject?
    }

    fun addObject(obj: FieldObject) {
        synchronized(objects) {
            objects[obj.fieldObjectType]?.add(obj)
        }
    }

    fun removeObject(obj: FieldObject) {
        synchronized(objects) {
            objects[obj.fieldObjectType]?.remove(obj)
                    ?: throw NullPointerException("[Field] Removal of field object failed.\n${toString()}")
        }
    }

    fun getObjects(): Map<FieldObjectType, MutableSet<FieldObject>> {
        synchronized(objects) {
            return HashMap(objects)
        }
    }

    fun getObjects(vararg t: FieldObjectType): Set<FieldObject> {
        synchronized(objects) {
            val objects: MutableSet<FieldObject> = HashSet()
            for (type in t) {
                objects.addAll(this.objects[type]!!)
            }
            return objects
        }
    }

    /**
     * Try not to use this version too much, in most cases you should already know the type.
     * Use [.getObject] instead
     *
     * @param i obj id
     * @return FieldObject of any type
     */
    fun getObject(i: Int): FieldObject? {
        synchronized(objects) {
            for (type in objects.keys) {
                for (obj in getObjects(type)) {
                    if (obj.id == i) return obj
                }
            }
            return null
        }
    }

    fun getObject(t: FieldObjectType, i: Int): FieldObject? {
        for (obj in getObjects(t)) {
            if (obj.id == i) return obj
        }
        return null
    }

    fun getClosestSpawnpoint(point: Point): FieldPortal {
        var sp: FieldPortal? = null
        var shortestDistance = Double.POSITIVE_INFINITY
        portals.values.forEach {
            val distance: Double = it.position.distanceSq(point)
            if (it.type == PortalType.START_POINT && distance < shortestDistance && it.targetMap == 999999999) {
                sp = it
                shortestDistance = distance
            }
        }
        return sp ?: firstSpawnpoint
    }

    private val firstSpawnpoint: FieldPortal
        get() = portals.values.stream().filter { it.type == PortalType.START_POINT }.findFirst().get()

    fun getPortalByName(name: String): FieldPortal? {
        return portals.values.stream().filter { it.name == name }.findFirst().orElse(null)
    }

    private fun getFhByPortal(portal: FieldPortal): Foothold {
        return footholds.values.stream()
                .filter {
                    it.x1 <= portal.position.getX() && it.x2 >= portal.position.getX()
                }
                .filter { it.x1 < it.x2 }
                .findFirst().get()
    }

    //this.objects.values().forEach(ret::addAll);
    val objectsAsList: Set<Any?>
        get() {
            val ret: MutableSet<FieldObject?> = HashSet()
            for (fieldObjects in objects.values) {
                println(fieldObjects)
                for (fieldObject in fieldObjects) {
                    println(fieldObject)
                    ret.add(fieldObject)
                }
            }
            //this.objects.values().forEach(ret::addAll);
            return ret
        }

    override fun toString(): String {
        return "Field{id=$id, runningObjectId=$runningObjectId, name='$name', objects=$objects}"
    }

    fun queueRespawn(mob: Int, cooldown: Int, time: Long) {
        toRespawn.add(Respawn(mob, cooldown, time))
    }

    private data class Respawn(val mob: Int, val cooldown: Int, val time: Long)
}