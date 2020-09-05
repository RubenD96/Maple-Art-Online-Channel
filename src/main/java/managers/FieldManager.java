package managers;

import field.Field;
import field.object.Foothold;
import field.object.life.*;
import field.object.portal.FieldPortal;
import managers.flag.FieldFlag;
import util.packet.PacketReader;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class FieldManager extends AbstractManager {

    private final Map<Integer, Field> fields = new HashMap<>();

    public Map<Integer, Field> getFields() {
        return fields;
    }

    public synchronized Field getField(int id) {
        Field field = fields.get(id);
        if (field == null) {
            field = new Field(id);
            field.init();
            if (!loadFieldData(field)) {
                return null;
            }
            fields.put(id, field);
        }
        return field;
    }

    public synchronized void reloadField(int id) {
        fields.remove(id);
    }

    private boolean loadFieldData(Field field) {
        PacketReader r = getData("wz/Map/" + field.getId() + ".mao");
        if (r != null) {
            int flags = r.readInteger();

            if (containsFlag(flags, FieldFlag.ID))
                r.readInteger();

            if (containsFlag(flags, FieldFlag.RETURN_MAP))
                field.setReturnMap(r.readInteger());

            if (containsFlag(flags, FieldFlag.MAP_AREA))
                field.setMapArea(r.readRectangle());

            if (containsFlag(flags, FieldFlag.FOOTHOLDS)) {
                short size = r.readShort();
                for (int i = 0; i < size; i++) {
                    Foothold fh = new Foothold();
                    fh.decode(r);
                    field.getFootholds().put(fh.getId(), fh);
                }
            }

            field.setForcedReturnMap(containsFlag(flags, FieldFlag.FORCED_RETURN) ? r.readInteger() : field.getId());

            if (containsFlag(flags, FieldFlag.FIELD_LIMIT))
                field.setFieldLimit(r.readInteger());

            if (containsFlag(flags, FieldFlag.NAME))
                field.setName(r.readMapleString());

            if (containsFlag(flags, FieldFlag.ON_ENTER))
                field.setScript(r.readMapleString());

            if (containsFlag(flags, FieldFlag.PORTALS)) {
                short size = r.readShort();
                for (int i = 0; i < size; i++) {
                    FieldPortal portal = new FieldPortal(field);
                    portal.generate(r);
                    field.getPortals().put((byte) portal.getId(), portal);
                }
            }

            if (containsFlag(flags, FieldFlag.AREAS)) {
                short size = r.readShort();
                for (int i = 0; i < size; i++) {
                    r.readRectangle();
                }
            }

            if (containsFlag(flags, FieldFlag.LIFE)) {
                short size = r.readShort();
                for (int i = 0; i < size; i++) {
                    AbstractFieldControlledLife obj;

                    int id = r.readInteger();
                    int time = r.readInteger();
                    int x = r.readInteger();
                    int y = r.readInteger();
                    int f = r.readInteger();
                    int hide = r.readInteger();
                    int fh = r.readInteger();
                    int cy = r.readInteger();
                    int rx0 = r.readInteger();
                    int rx1 = r.readInteger();
                    String type = r.readMapleString();
                    if (type.equals("m")) {
                        FieldMobTemplate template = MobManager.getMob(id);
                        if (template == null) { // mob data doesn't exist
                            continue;
                        }
                        FieldMob mob = new FieldMob(template, f == 1);
                        mob.setHp(mob.getTemplate().getMaxHP());
                        mob.setMp(mob.getTemplate().getMaxMP());
                        mob.setHome((short) fh);
                        if (time == 0) time = 5;
                        mob.setTime(time);
                        obj = mob;

                        field.getMobSpawnPoints().add(new FieldMobSpawnPoint(id, new Point(x, y), rx0, rx1, cy, time, (short) fh));
                    } else { // npc
                        FieldNPC template = NPCManager.getNPC(id);
                        if (template == null) continue;

                        FieldNPC npc = new FieldNPC(template);
                        obj = npc;
                    }
                    obj.setRx0(rx0);
                    obj.setRx1(rx1);
                    obj.setPosition(new Point(x, y));
                    obj.setFoothold((short) fh);
                    obj.setF(f == 0);
                    obj.setCy(cy);
                    obj.setHide(hide == 1);
                    field.enter(obj);
                }
            }
            //System.out.println("Finished initializing field: " + field.getId());
            return true;
        } else {
            System.err.println("Field " + field.getId() + " does not exist!");
        }
        return false;
    }

    private boolean containsFlag(int flags, FieldFlag flag) {
        return (flags & flag.getValue()) == flag.getValue();
    }
}
