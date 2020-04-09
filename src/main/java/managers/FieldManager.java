package managers;

import field.Field;
import field.object.Foothold;
import field.object.life.AbstractFieldControlledLife;
import field.object.life.FieldMob;
import field.object.life.FieldNPC;
import field.object.portal.FieldPortal;
import lombok.Getter;
import managers.flag.FieldFlag;
import util.packet.PacketReader;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Getter
public class FieldManager extends AbstractManager {

    private Map<Integer, Field> fields = new HashMap<>();

    public synchronized Field getField(int id) {
        Field field = fields.get(id);
        if (field == null) {
            field = new Field(id);
            field.init();
            loadFieldData(field);
            fields.put(id, field);
        }
        return field;
    }

    private void loadFieldData(Field field) {
        PacketReader r = getData("wz/Map/" + field.getId() + ".mao");
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
                fh.generate(r);
                field.getFootholds().put(fh.getId(), fh);
            }
        }

        if (containsFlag(flags, FieldFlag.FORCED_RETURN))
            field.setForcedReturnMap(r.readInteger());

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
                    FieldMob mob = new FieldMob(MobManager.getMob(id), f == 1);
                    mob.setHp(mob.getTemplate().getMaxHP());
                    mob.setMp(mob.getTemplate().getMaxMP());
                    mob.setHome((short) fh);
                    obj = mob;
                } else { // npc
                    FieldNPC npc = NPCManager.getNPC(id);
                    obj = npc;
                }
                obj.setRx0(rx0);
                obj.setRx1(rx1);
                obj.setPosition(new Point(x, y));
                obj.setFoothold((short) fh);
                obj.setF(f == 1);
                obj.setCy(cy);
                obj.setHide(hide == 1);
                field.enter(obj);
            }
        }
        System.out.println("Finished initializing field: " + field.getId());
    }

    public boolean containsFlag(int flags, FieldFlag flag) {
        return (flags & flag.getValue()) == flag.getValue();
    }
}