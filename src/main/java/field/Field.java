package field;

import client.Character;
import client.party.PartyMember;
import client.player.quest.Quest;
import client.player.quest.QuestState;
import field.object.FieldObject;
import field.object.FieldObjectType;
import field.object.Foothold;
import field.object.drop.AbstractFieldDrop;
import field.object.drop.EnterType;
import field.object.life.FieldControlledObject;
import field.object.life.FieldMob;
import field.object.life.FieldMobSpawnPoint;
import field.object.life.FieldMobTemplate;
import field.object.portal.FieldPortal;
import field.object.portal.PortalType;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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

@Data
public class Field {

    @NonNull private final int id;
    private static AtomicInteger runningObjectId = new AtomicInteger(1000000000);
    private int returnMap, forcedReturnMap, fieldLimit;
    private String name, script;
    private Rectangle mapArea;
    private final Map<Byte, FieldPortal> portals = new HashMap<>();
    private final Map<Integer, Foothold> footholds = new HashMap<>();
    private final Map<FieldObjectType, Set<FieldObject>> objects = new LinkedHashMap<>();
    private final List<FieldMobSpawnPoint> mobSpawnPoints = new ArrayList<>();
    private final List<Respawn> toRespawn = new ArrayList<>();

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
                        mem.write(PartyPackets.updateParty(chr.getParty(), member.getChannel()));
                        if (member.getField() == id) {
                            mem.updatePartyHP(true);
                        }
                    }
                });
            }

            if (!script.isEmpty()) {
                FieldScriptManager.getInstance().execute(chr.getClient(), this, script);
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

    @RequiredArgsConstructor
    private static class Respawn {
        private final int mob;
        private final int cooldown;
        private final long time;
    }
}
