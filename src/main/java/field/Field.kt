package field

import client.Character
import client.party.PartyMember
import client.player.quest.QuestState
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
import scripting.map.FieldScriptManager.execute
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

    val portals: MutableMap<Byte, FieldPortal> = HashMap()
    val footholds: MutableMap<Int, Foothold> = HashMap()
    private val objects: MutableMap<FieldObjectType, MutableSet<FieldObject>> = EnumMap(FieldObjectType::class.java)
    val mobSpawnPoints: MutableList<FieldMobSpawnPoint> = ArrayList<FieldMobSpawnPoint>()
    private val toRespawn: MutableList<Respawn> = ArrayList()

    companion object {
        private val runningObjectId = AtomicInteger(1000000000)
    }

    init {
        for (type in FieldObjectType.values()) {
            objects[type] = LinkedHashSet()
        }
    }

    fun broadcast(packet: Packet, source: Character? = null) {
        getObjects(FieldObjectType.CHARACTER).stream()
                .filter { it !== source }
                .forEach { (it as Character).write(packet.clone()) }
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
                                    is AbstractFieldDrop -> {
                                        enterItemDrop(it, it.enterFieldPacket)
                                    }
                                    is FieldMob -> {
                                        obj.write(it.getEnterFieldPacket(MobSummonType.NORMAL))
                                    }
                                    else -> {
                                        obj.write(it.enterFieldPacket)
                                    }
                                }
                            }
                }
            }

            if (obj.party != null) {
                val me = obj.party?.getMember(obj.id)
                me!!.field = id
                obj.party?.getMembers()?.forEach { member: PartyMember ->
                    if (member.isOnline && member.channel == obj.getChannel().channelId) {
                        val mem = obj.getChannel().getCharacter(member.cid)
                        mem!!.write(updateParty(obj.party!!, member.channel))
                        if (member.field == id) {
                            mem.updatePartyHP(true)
                        }
                    }
                }
            }

            if (script.isNotEmpty()) {
                execute(obj.client, this, script)
            }
        } else {
            obj.id = runningObjectId.addAndGet(1)
            if (obj.fieldObjectType === FieldObjectType.DROP) {
                val drop: AbstractFieldDrop = obj as AbstractFieldDrop
                enterItemDrop(drop, drop.getEnterFieldPacket(EnterType.PARTY))
            } else {
                broadcast(obj.enterFieldPacket)
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

    @JvmOverloads
    fun leave(obj: FieldObject, leaveFieldPacket: Packet? = null) {
        removeObject(obj)
        if (obj is Character) {
            broadcast(obj.leaveFieldPacket, obj)
        } else {
            broadcast(leaveFieldPacket!!)
        }
        updateControlledObjects()
    }

    fun respawn() {
        synchronized(toRespawn) {
            val spawned: MutableList<Respawn> = ArrayList()
            val time = System.currentTimeMillis()
            toRespawn.forEach {
                if (time > it.time) {
                    val newSpawn: FieldMobSpawnPoint = getRandomViableMobSpawnPoint(it.mob)
                    val template: FieldMobTemplate = MobManager.getMob(it.mob)
                    val mob = FieldMob(template, false)
                    mob.hp = mob.template.maxHP
                    mob.mp = mob.template.maxMP
                    mob.home = newSpawn.fh
                    mob.time = it.cooldown
                    mob.rx0 = newSpawn.rx0
                    mob.rx1 = newSpawn.rx1
                    mob.position = newSpawn.point
                    mob.foothold = newSpawn.fh
                    mob.cy = newSpawn.cy
                    mob.hide = false
                    mob.field = this
                    enter(mob)
                    spawned.add(it)
                }
            }
            spawned.forEach { toRespawn.remove(it) }
        }
    }

    private fun getRandomViableMobSpawnPoint(mob: Int): FieldMobSpawnPoint {
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

/*
package field;

import client.Character;
import client.party.PartyMember;
import client.player.quest.Quest;
import client.player.quest.QuestState;
import field.obj.FieldObject;
import field.obj.FieldObjectType;
import field.obj.Foothold;
import field.obj.drop.AbstractFieldDrop;
import field.obj.drop.EnterType;
import field.obj.life.*;
import field.obj.portal.FieldPortal;
import field.obj.portal.PortalType;
import managers.MobManager;
import net.maple.packets.FieldPackets;
import net.maple.packets.PartyPackets;
import scripting.map.FieldScriptManager;
import util.packet.Packet;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Field {

    private final int id;
    private static final AtomicInteger runningObjectId = new AtomicInteger(1000000000);
    private int returnMap, forcedReturnMap, fieldLimit;
    private String name, script;
    private Rectangle mapArea;
    private final Map<Byte, FieldPortal> portals = new HashMap<>();
    private final Map<Integer, Foothold> footholds = new HashMap<>();
    private final Map<FieldObjectType, Set<FieldObject>> objects = new LinkedHashMap<>();
    private final List<FieldMobSpawnPoint> mobSpawnPoints = new ArrayList<>();
    private final List<Respawn> toRespawn = new ArrayList<>();

    public Field(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setReturnMap(int returnMap) {
        this.returnMap = returnMap;
    }

    public int getForcedReturnMap() {
        return forcedReturnMap;
    }

    public void setForcedReturnMap(int forcedReturnMap) {
        this.forcedReturnMap = forcedReturnMap;
    }

    public void setFieldLimit(int fieldLimit) {
        this.fieldLimit = fieldLimit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Rectangle getMapArea() {
        return mapArea;
    }

    public void setMapArea(Rectangle mapArea) {
        this.mapArea = mapArea;
    }

    public Map<Byte, FieldPortal> getPortals() {
        return portals;
    }

    public Map<Integer, Foothold> getFootholds() {
        return footholds;
    }

    public List<FieldMobSpawnPoint> getMobSpawnPoints() {
        return mobSpawnPoints;
    }

    public void init() {
        for (FieldObjectType type : FieldObjectType.values()) {
            objects.put(type, new LinkedHashSet<>());
        }
    }

    public void broadcast(Packet packet, Character source) {
        getObjects(FieldObjectType.CHARACTER).stream()
                .filter(chr -> !chr.equals(source))
                .forEach(chr -> ((Character) chr).write(packet.clone()));
    }

    public void broadcast(Packet packet) {
        broadcast(packet, null);
    }

    public synchronized void enter(Character chr, String portal) {
        enter(chr, (byte) getPortalByName(portal).getId());
    }

    public synchronized void enter(Character chr, byte portal) {
        chr.setPortal(portal);
        enter(chr);
    }

    public synchronized void enter(FieldObject obj) {
        if (obj.getField() != null) {
            obj.getField().leave(obj, obj.getLeaveFieldPacket());
        }
        obj.setField(this);

        addObject(obj);
        if (obj instanceof Character) {
            Character chr = (Character) obj;

            FieldPortal portal = portals.getOrDefault(chr.getPortal(), getFirstSpawnpoint());

            obj.setId(chr.getId());
            chr.setFieldId(id);
            chr.setPosition(portal.getPosition());
            chr.setFoothold((short) (portal.getType() != PortalType.START_POINT
                    ? getFhByPortal(portal).getId()
                    : 0
            ));

            chr.write(FieldPackets.setField(chr));
            broadcast(chr.getEnterFieldPacket(), chr);
            objects.values().forEach(set -> set.stream()
                    .filter(o -> !o.equals(obj))
                    .forEach(o -> {
                        if (o instanceof AbstractFieldDrop) {
                            enterItemDrop((AbstractFieldDrop) o, o.getEnterFieldPacket());
                        } else if (o instanceof FieldMob) {
                            chr.write(((FieldMob) o).getEnterFieldPacket(MobSummonType.NORMAL));
                        } else {
                            chr.write(o.getEnterFieldPacket());
                        }
                    }));

            if (chr.getParty() != null) {
                PartyMember me = chr.getParty().getMember(chr.getId());
                me.setField(id);
                chr.getParty().getMembers().forEach(member -> {
                    if (member.isOnline() && member.getChannel() == chr.getChannel().getChannelId()) {
                        Character mem = chr.getChannel().getCharacter(member.getCid());
                        mem.write(PartyPackets.INSTANCE.updateParty(chr.getParty(), member.getChannel()));
                        if (member.getField() == id) {
                            mem.updatePartyHP(true);
                        }
                    }
                });
            }

            if (!script.isEmpty()) {
                FieldScriptManager.INSTANCE.execute(chr.getClient(), this, script);
            }
        } else {
            obj.setId(runningObjectId.addAndGet(1));
            if (obj.getFieldObjectType() == FieldObjectType.DROP) {
                AbstractFieldDrop drop = (AbstractFieldDrop) obj;
                enterItemDrop(drop, drop.getEnterFieldPacket(EnterType.PARTY));
            } else {
                broadcast(obj.getEnterFieldPacket());
            }
        }

        updateControlledObjects();
    }

    private void enterItemDrop(AbstractFieldDrop drop, Packet enterPacket) {
        getObjects(FieldObjectType.CHARACTER).forEach(c -> {
            Character chr = (Character) c;
            if (drop.getQuestId() != 0) {
                Quest quest = chr.getQuests().get(drop.getQuestId());
                if (quest != null && quest.getState() != QuestState.PERFORM) {
                    return;
                }
            }
            chr.write(enterPacket.clone());
        });
    }

    public synchronized void leave(FieldObject obj) {
        leave(obj, null);
    }

    public synchronized void leave(FieldObject obj, Packet leaveFieldPacket) {
        removeObject(obj);
        if (obj instanceof Character) {
            Character chr = (Character) obj;
            broadcast(chr.getLeaveFieldPacket(), chr);
        } else {
            broadcast(leaveFieldPacket);
        }

        updateControlledObjects();
    }

    public void respawn() {
        List<Respawn> spawned = new ArrayList<>();
        long time = System.currentTimeMillis();
        toRespawn.forEach(respawn -> {
            if (time > respawn.time) {
                FieldMobSpawnPoint newSpawn = getRandomViableMobSpawnPoint(respawn.mob);

                FieldMobTemplate template = MobManager.getMob(respawn.mob);
                FieldMob mob = new FieldMob(template, false);
                mob.setHp(mob.getTemplate().getMaxHP());
                mob.setMp(mob.getTemplate().getMaxMP());
                mob.setHome(newSpawn.getFh());
                mob.setTime(respawn.cooldown);

                mob.setRx0(newSpawn.getRx0());
                mob.setRx1(newSpawn.getRx1());
                mob.setPosition(newSpawn.getPoint());
                mob.setFoothold(newSpawn.getFh());
                mob.setCy(newSpawn.getCy());
                mob.setHide(false);
                enter(mob);

                spawned.add(respawn);
            }
        });

        spawned.forEach(toRespawn::remove);
    }

    public FieldMobSpawnPoint getRandomViableMobSpawnPoint(int mob) {
        Object[] spawnPoints = mobSpawnPoints.stream().filter(sp -> sp.getId() == mob).toArray();
        return (FieldMobSpawnPoint) spawnPoints[new Random().nextInt(spawnPoints.length)];
    }

    public void removeExpiredDrops() {
        List<AbstractFieldDrop> drops = new ArrayList<>();
        long time = System.currentTimeMillis();
        getObjects(FieldObjectType.DROP).forEach(obj -> {
            AbstractFieldDrop drop = (AbstractFieldDrop) obj;
            if (time > drop.getExpire()) {
                drops.add(drop);
            }
        });

        drops.forEach(drop -> leave(drop, drop.getLeaveFieldPacket()));
    }

    public synchronized void updateControlledObjects() {
        List<FieldObject> characters = getObjects(FieldObjectType.CHARACTER).stream()
                .sorted(Comparator.comparingInt(chr -> ((Character) chr).getControlledObjects().size()))
                .collect(Collectors.toList());

        FieldObjectType[] types = {FieldObjectType.NPC, FieldObjectType.MOB};
        List<FieldObject> controlled = getObjects(types).stream()
                .filter(obj -> obj instanceof FieldControlledObject)
                .collect(Collectors.toList());

        controlled.stream().filter(obj -> {
            FieldControlledObject c = ((FieldControlledObject) obj);
            return c.getController() == null || !characters.contains(c.getController());
        }).forEach(c -> ((FieldControlledObject) c).setController((Character) characters.stream().findFirst().orElse(null)));
    }

    public FieldControlledObject getControlledObject(Character chr, int oid) {
        FieldObjectType[] types = {FieldObjectType.NPC, FieldObjectType.MOB};
        return (FieldControlledObject) getObjects(types).stream().filter(
                o -> ((FieldControlledObject) o).getController().equals(chr) &&
                        o.getId() == oid).findAny().orElse(null);
    }

    public void addObject(FieldObject obj) {
        objects.get(obj.getFieldObjectType()).add(obj);
    }

    public void removeObject(FieldObject obj) {
        boolean success = objects.get(obj.getFieldObjectType()).remove(obj);
        if (!success) {
            throw new NullPointerException("[Field] Removal of field object failed.\n" + toString());
        }
    }

    public Map<FieldObjectType, Set<FieldObject>> getObjects() {
        return objects;
    }

    public Set<FieldObject> getObjects(FieldObjectType... t) {
        Set<FieldObject> objects = new HashSet<>();
        for (FieldObjectType type : t) {
            objects.addAll(this.objects.get(type));
        }
        return objects;
    }

    /**
     * Try not to use this version too much, in most cases you should already know the type.
     * Use {@link #getObject(FieldObjectType, int)} instead
     *
     * @param i obj id
     * @return FieldObject of any type
     */
    public FieldObject getObject(int i) {
        for (FieldObjectType type : objects.keySet()) {
            for (FieldObject obj : getObjects(type)) {
                if (obj.getId() == i) return obj;
            }
        }
        return null;
    }

    public FieldObject getObject(FieldObjectType t, int i) {
        for (FieldObject obj : getObjects(t)) {
            if (obj.getId() == i) return obj;
        }
        return null;
    }

    public FieldPortal getClosestSpawnpoint(Point point) {
        FieldPortal sp = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (FieldPortal portal : portals.values()) {
            double distance = portal.getPosition().distanceSq(point);
            if (portal.getType() == PortalType.START_POINT && distance < shortestDistance && portal.getTargetMap() == 999999999) {
                sp = portal;
                shortestDistance = distance;
            }
        }
        return sp;
    }

    public FieldPortal getFirstSpawnpoint() {
        return portals.values().stream().filter(p -> p.getType() == PortalType.START_POINT).findFirst().orElse(null);
    }

    public FieldPortal getPortalByName(String name) {
        return portals.values().stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }

    public Foothold getFhByPortal(FieldPortal portal) {
        return footholds.values().stream()
                .filter(fh -> fh.getX1() <= portal.getPosition().getX() &&
                        fh.getX2() >= portal.getPosition().getX())
                .filter(fh -> fh.getX1() < fh.getX2())
                .findFirst().get();
    }

    public Set<FieldObject> getObjectsAsList() {
        Set<FieldObject> ret = new HashSet<>();
        for (Set<FieldObject> fieldObjects : objects.values()) {
            System.out.println(fieldObjects);
            for (FieldObject fieldObject : fieldObjects) {
                System.out.println(fieldObject);
                ret.add(fieldObject);
            }
        }
        //this.objects.values().forEach(ret::addAll);
        return ret;
    }

    @Override
    public String toString() {
        return "Field{" +
                "id=" + id +
                ", runningObjectId=" + runningObjectId +
                ", name='" + name + '\'' +
                ", objects=" + objects +
                '}';
    }

    public void queueRespawn(int mob, int cooldown, long time) {
        toRespawn.add(new Respawn(mob, cooldown, time));
    }

    private static class Respawn {
        private final int mob;
        private final int cooldown;
        private final long time;

        public Respawn(int mob, int cooldown, long time) {
            this.mob = mob;
            this.cooldown = cooldown;
            this.time = time;
        }
    }
}
 */