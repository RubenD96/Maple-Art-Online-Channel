package managers;

import field.object.life.FieldMobTemplate;
import util.packet.PacketReader;

import java.util.HashMap;
import java.util.Map;

public class MobManager extends AbstractManager {

    private static Map<Integer, FieldMobTemplate> mobs = new HashMap<>();

    public static synchronized FieldMobTemplate getMob(int id) {
        FieldMobTemplate mob = mobs.get(id);
        if (mob == null) {
            mob = new FieldMobTemplate(id);
            loadMobData(mob);
            mobs.put(id, mob);
        }
        return mob;
    }

    private static void loadMobData(FieldMobTemplate mob) {
        PacketReader r = getData("wz/Mob/" + mob.getId() + ".mao");

        if (r != null) {
            mob.setLevel(r.readShort());
            mob.setName(r.readMapleString());
            mob.setExp(r.readInteger());
            mob.setMaxHP(r.readInteger());
            mob.setMaxMP(r.readInteger());
        }
    }
}
