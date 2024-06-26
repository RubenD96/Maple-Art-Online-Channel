package field

import client.Avatar
import client.Character
import client.player.quest.QuestState
import client.replay.MoveCollection
import client.replay.Replay
import constants.FieldConstants.JQ_FIELDS
import field.obj.FieldObject
import field.obj.drop.AbstractFieldDrop
import field.obj.drop.OwnerType
import field.obj.life.*
import field.obj.portal.FieldPortal
import field.obj.portal.PortalType
import field.obj.reactor.FieldReactor
import managers.MobManager
import moe.maple.miho.foothold.Foothold
import net.maple.packets.FieldPackets.setField
import net.maple.packets.PartyPackets.updateParty
import net.server.Server.getCharacter
import scripting.field.FieldScriptManager
import util.logging.LogType
import util.logging.Logger
import util.packet.Packet
import java.awt.Point
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.reflect.KClass

open class Field(val template: FieldTemplate) {

    var replay: Replay? = null
    val portals: Map<Byte, FieldPortal> = HashMap(template.portals).also {
        it.values.forEach { portal ->
            portal.field = this
        }
    }

    //CHARACTER, SUMMONED, MOB, NPC, DROP, TOWN_PORTAL, REACTOR, ETC, REPLAY
    val fieldObjects: Map<KClass<out FieldObject>, MutableSet<FieldObject>> =
        mapOf(
            Character::class to mutableSetOf(),
            FieldMob::class to mutableSetOf(),
            AbstractFieldDrop::class to mutableSetOf(),
            FieldReactor::class to mutableSetOf(),
            Replay::class to mutableSetOf(),
            FieldNPC::class to mutableSetOf()
        )

    private val toRespawn: MutableList<Respawn> = ArrayList()
    lateinit var bossDamage: Map<String, Int>

    companion object {
        //private val runningObjectId = AtomicInteger(1000000000)
        val fieldIds: Map<KClass<out FieldObject>, AtomicInteger> =
            mapOf(
                FieldMob::class to AtomicInteger(0),
                AbstractFieldDrop::class to AtomicInteger(0),
                FieldReactor::class to AtomicInteger(0),
                Replay::class to AtomicInteger(999999999),
                FieldNPC::class to AtomicInteger(0)
            )
    }

    fun startReplay() {
        replay?.stop()
        replay = Replay().load(this)?.also { enter(it) }
    }

    init {
        if (JQ_FIELDS.contains(template.id)) {
            startReplay()
        }
    }

    fun broadcast(packet: Packet, source: Character? = null) {
        getObjects<Character>()
            .filter { it !== source }
            .forEach { it.write(packet.clone()) }
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
            with(template) {
                val portal: FieldPortal = portals[obj.portal] ?: firstSpawnpoint
                obj.id = obj.id
                obj.fieldId = id
                obj.moveCollections[id] = MoveCollection(obj, id)
                if (!obj.chasing) obj.position = portal.position
                obj.foothold = (if (portal.type != PortalType.START_POINT) getFhByPortal(portal).id() else 0).toShort()
                obj.write(obj.setField())

                obj.pets.forEach {
                    it.field = obj.field
                    it.position = obj.position
                    it.foothold = obj.foothold
                    obj.write(it.enterFieldPacket)
                }

                broadcast(obj.enterFieldPacket, obj)

                // Show the player what objects are present on the field
                synchronized(fieldObjects) {
                    fieldObjects.values.forEach { set ->
                        set.stream()
                            .filter { it != obj }
                            .forEach {
                                when (it) {
                                    is AbstractFieldDrop -> enterItemDrop(
                                        it,
                                        it.enterFieldPacket,
                                        OwnerType.FFA,
                                        obj
                                    ) // todo uuh
                                    is FieldMob -> obj.write(it.getEnterFieldPacket(MobSummonType.NORMAL))
                                    is Character -> {
                                        obj.write(it.enterFieldPacket)
                                        it.pets.forEach { pet ->
                                            obj.write(pet.enterFieldPacket)
                                        }
                                    }
                                    else -> obj.write(it.enterFieldPacket)
                                }
                            }
                    }
                }

                // Let party know you changed field
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
            }
            //if (template.script.isNotEmpty()) { // never happens as we set script to mapid when onEnter is not present
            FieldScriptManager[template.script]?.execute(obj.client)
            //}
        } else { // not a character object
            //obj.id = runningObjectId.addAndGet(1)
            obj.id = fieldIds[obj.kclass]?.addAndGet(1) ?: error("Invalid field object for id | type: ${obj::class.simpleName}")
            when (obj) {
                is AbstractFieldDrop -> enterItemDrop(
                    obj,
                    obj.getEnterFieldPacket(OwnerType.PARTY),
                    OwnerType.PARTY,
                    characters = getObjects<Character>().toTypedArray()
                )
                is Replay -> {
                    broadcast(obj.enterFieldPacket)
                    obj.start()
                }
                else -> broadcast(obj.enterFieldPacket)
            }
        }
        updateControlledObjects()
    }

    private fun enterItemDrop(drop: AbstractFieldDrop, enterPacket: Packet, type: Byte, vararg characters: Character) {
        characters.forEach {
            if (drop.questId != 0) {
                val quest = it.quests[drop.questId]
                if (quest != null && quest.state !== QuestState.PERFORM) {
                    return@forEach
                }
            }

            it.cursed?.let { curse ->
                it.write(drop.getEnterFieldPacket(type, curse))
            } ?: run {
                it.write(enterPacket.clone())
            }
        }
    }

    fun leave(avatar: Avatar) {
        removeObject(avatar)
        when (avatar) {
            is Character -> {
                broadcast(avatar.leaveFieldPacket, avatar)
            }
            is Replay -> {
                broadcast(avatar.leaveFieldPacket)
                replay = null
            }
        }
        updateControlledObjects()
    }

    /**
     * Do NOT use for FieldObjects that extend from Avatar
     */
    fun leave(obj: FieldObject, leaveFieldPacket: Packet) {
        if (obj is Avatar) {
            leave(obj)
        } else {
            removeObject(obj)
            broadcast(leaveFieldPacket)
            updateControlledObjects()
        }
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
                        mob.position = it.position
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

    private fun getRandomViableMobSpawnPoint(mob: Int): FieldLifeSpawnPoint? {
        with(template) {
            if (mobSpawnPoints.isEmpty()) return null // spawned in a map with no mob spawns, don't respawn

            val spawnPoints = mobSpawnPoints.stream().filter { sp: FieldLifeSpawnPoint -> sp.id == mob }.toArray()
            return spawnPoints[Random().nextInt(spawnPoints.size)] as FieldLifeSpawnPoint
        }
    }

    fun removeExpiredDrops() {
        val drops: MutableList<AbstractFieldDrop> = ArrayList()
        val time = System.currentTimeMillis()
        getObjects<AbstractFieldDrop>().forEach {
            if (time > it.expire) {
                drops.add(it)
            }
        }
        drops.forEach { leave(it, it.leaveFieldPacket) }
    }

    @Synchronized
    fun updateControlledObjects() {
        val characters: List<Character> = getObjects<Character>().stream()
            .sorted(Comparator.comparingInt { it.controlledObjects.size })
            .collect(Collectors.toList())

        val controlled: Set<FieldControlledObject> = getObjects<FieldNPC>() + getObjects<FieldMob>()

        controlled.stream()
            .filter {
                it.controller == null || !characters.contains(it.controller)
            }.forEach {
                it.controller = characters.stream().findFirst().orElse(null)
            }
    }

    @Synchronized
    fun getControlledObject(chr: Character, oid: Int): FieldControlledObject? {
        return (getObjects<FieldNPC>() + getObjects<FieldMob>()).stream()
            .filter {
                it.controller == chr && it.id == oid
            }.findAny().orElse(null)
    }

    fun addObject(obj: FieldObject) {
        synchronized(fieldObjects) {
            fieldObjects[obj.kclass]?.add(obj) ?: error("Invalid field object type: ${obj::class.simpleName}")
        }
    }

    fun removeObject(obj: FieldObject) {
        synchronized(fieldObjects) {
            fieldObjects[obj.kclass]?.remove(obj)
                ?: throw NullPointerException("[Field] Removal of field object failed.\n${toString()}")
        }
    }

    @Deprecated(message = "Use the one that uses generics: getObjects<FieldObject>()")
    fun getObjects(): HashMap<KClass<out FieldObject>, MutableSet<FieldObject>> {
        synchronized(fieldObjects) {
            return HashMap(fieldObjects)
        }
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : FieldObject> getObjects(): Set<T> {
        synchronized(fieldObjects) {
            return (fieldObjects[T::class]
                ?: error("Type ${T::class.simpleName} does not exist in field")).toSet() as Set<T>
        }
    }

    inline fun <reified T : FieldObject> getObject(id: Int): T? {
        synchronized(fieldObjects) {
            return fieldObjects[T::class]?.firstOrNull { it.id == id } as? T
        }
    }

    @Deprecated(message = "Use the one that uses generics: getObject<FieldObject>(Int)")
    fun getGenericObject(id: Int): FieldObject? {
        synchronized(fieldObjects) {
            fieldObjects.forEach {
                it.value.forEach { obj ->
                    if (obj.id == id) {
                        return obj
                    }
                }
            }
        }
        return null
    }

    fun isEmpty(): Boolean {
        return getObjects<Character>().isEmpty()
    }

    fun getClosestSpawnpoint(point: Point): FieldPortal {
        var sp: FieldPortal? = null
        var shortestDistance = Double.POSITIVE_INFINITY
        template.portals.values.forEach {
            val distance: Double = it.position.distanceSq(point)
            if (it.type == PortalType.START_POINT && distance < shortestDistance && it.targetMap == 999999999) {
                sp = it
                shortestDistance = distance
            }
        }
        return sp ?: firstSpawnpoint
    }

    private val firstSpawnpoint: FieldPortal
        get() = template.portals.values.stream().filter { it.type == PortalType.START_POINT }.findFirst().get()

    fun getPortalByName(name: String): FieldPortal? {
        return template.portals.values.stream().filter { it.name == name }.findFirst().orElse(null)
    }

    private fun getFhByPortal(portal: FieldPortal): Foothold {
        return template.footholds.getFootholdUnderneath(portal.position.x, portal.position.y)
    }

    override fun toString(): String {
        return "Field{id=${template.id}, name='${template.mapName}', objects=$fieldObjects}"
    }

    fun queueRespawn(mob: Int, cooldown: Int, time: Long) {
        toRespawn.add(Respawn(mob, cooldown, time))
    }

    private data class Respawn(val mob: Int, val cooldown: Int, val time: Long)
}