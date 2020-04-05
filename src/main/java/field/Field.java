package field;

import client.Character;
import field.object.FieldObject;
import field.object.FieldObjectType;
import field.object.Foothold;
import field.object.life.FieldControlledObject;
import field.object.portal.FieldPortal;
import field.object.portal.PortalType;
import lombok.Data;
import lombok.NonNull;
import net.maple.packets.FieldPackets;
import util.packet.Packet;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Data
public class Field {

    @NonNull private final int id;
    private AtomicInteger runningObjectId = new AtomicInteger(1000000000);
    private int returnMap, forcedReturnMap, fieldLimit;
    private String name, script;
    private Rectangle mapArea;
    private final Map<Byte, FieldPortal> portals = new HashMap<>();
    private final Map<Integer, Foothold> footholds = new HashMap<>();
    private final Map<FieldObjectType, Set<FieldObject>> objects = new HashMap<>();

    public void init() {
        for (FieldObjectType type : FieldObjectType.values()) {
            objects.put(type, new HashSet<>());
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

    public synchronized void enter(Character chr, byte portal) {
        chr.setPortal(portal);
        enter(chr);
    }

    public synchronized void enter(FieldObject obj) {
        if (obj.getField() != null) {
            obj.getField().leave(obj);
        }
        obj.setField(this);

        addObject(obj);
        if (obj instanceof Character) {
            Character chr = (Character) obj;

            FieldPortal portal = portals.getOrDefault(chr.getPortal(), getSpawnpoint());
            // TODO: 4/3/2020 chr.getPortal() is always 0

            obj.setId(chr.getId());
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
                        chr.write(o.getEnterFieldPacket());
                    }));
        } else {
            obj.setId(runningObjectId.addAndGet(1));
            broadcast(obj.getEnterFieldPacket());
        }

        updateControlledObjects();
    }

    public synchronized void leave(FieldObject obj) {
        removeObject(obj);
        if (obj instanceof Character) {
            Character chr = (Character) obj;
            broadcast(chr.getLeaveFieldPacket(), chr);
        } else {
            broadcast(obj.getLeaveFieldPacket());
        }

        updateControlledObjects();
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
        objects.get(obj.getFieldObjectType()).remove(obj);
    }

    public Set<FieldObject> getObjects(FieldObjectType... t) {
        Set<FieldObject> objects = new HashSet<>();
        for (FieldObjectType type : t) {
            objects.addAll(this.objects.get(type));
        }
        return objects;
    }

    public FieldObject getObject(FieldObjectType t, int i) {
        for (FieldObject obj : getObjects(t)) {
            if (obj.getId() == i) return obj;
        }
        return null;
    }

    public FieldPortal getSpawnpoint() {
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
}
