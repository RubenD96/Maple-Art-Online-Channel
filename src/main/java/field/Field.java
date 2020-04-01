package field;

import field.object.FieldObject;
import field.object.FieldObjectType;
import lombok.Data;
import lombok.NonNull;
import client.Character;
import util.packet.Packet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class Field {

    @NonNull final int id;
    //final List<Character> characters = new ArrayList<>();
    final Map<FieldObjectType, Set<FieldObject>> objects = new HashMap<>();

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

    public synchronized void enter(FieldObject obj) {
        if (obj.getField() != null) {
            leave(obj);
        }
        obj.setField(this);

        addObject(obj);
        if (obj instanceof Character) {
            Character chr = (Character) obj;
            broadcast(chr.getEnterFieldPacket(), chr);
            objects.values().forEach(set -> set.stream()
                    .filter(o -> !o.equals(obj))
                    .forEach(o -> {
                        System.out.println(o);
                        chr.write(o.getEnterFieldPacket());
                    }));
        } else {
            broadcast(obj.getEnterFieldPacket());
        }
    }

    public synchronized void leave(FieldObject obj) {
        removeObject(obj);
        if (obj instanceof Character) {
            Character chr = (Character) obj;
            broadcast(chr.getLeaveFieldPacket(), chr);
        } else {
            broadcast(obj.getLeaveFieldPacket());
        }
    }

    public void addObject(FieldObject obj) {
        objects.get(obj.getFieldObjectType()).add(obj);
    }

    public void removeObject(FieldObject obj) {
        objects.get(obj.getFieldObjectType()).remove(obj);
    }

    public Set<FieldObject> getObjects(FieldObjectType t) {
        return objects.get(t);
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
