package managers;

import field.obj.life.FieldMobTemplate;
import util.packet.PacketReader;

import java.util.HashMap;
import java.util.Map;

public class MobManager extends AbstractManager {

    private final static Map<Integer, FieldMobTemplate> mobs = new HashMap<>();

    public static synchronized FieldMobTemplate getMob(int id) {
        FieldMobTemplate mob = mobs.get(id);
        if (mob == null) {
            mob = new FieldMobTemplate(id);
            if (loadMobData(mob)) {
                mobs.put(id, mob);
            } else {
                return null;
            }
        }
        return mob;
    }

    private static boolean loadMobData(FieldMobTemplate mob) {
        PacketReader r = getData("wz/Mob/" + mob.getId() + ".mao");

        if (r != null) {
            mob.setLevel(r.readShort());
            mob.setName(r.readMapleString());
            mob.setExp(r.readInteger());
            mob.setMaxHP(r.readInteger());
            mob.setMaxMP(r.readInteger());
            mob.setBoss(r.readBool());
            return true;
        }
        return false;
    }
}
