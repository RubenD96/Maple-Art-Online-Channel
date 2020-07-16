package managers;

import field.object.life.FieldNPC;
import util.packet.PacketReader;

import java.util.HashMap;
import java.util.Map;

public class NPCManager extends AbstractManager {

    private final static Map<Integer, FieldNPC> npcs = new HashMap<>();

    public static synchronized FieldNPC getNPC(int id) {
        FieldNPC npc = npcs.get(id);
        if (npc == null) {
            npc = new FieldNPC(id);
            boolean exists = loadNPCData(npc);
            if (!exists) return null;
            npcs.put(id, npc);
        }
        return npc;
    }

    private static boolean loadNPCData(FieldNPC npc) {
        PacketReader r = getData("wz/Npc/" + npc.getNpcId() + ".mao");

        if (r == null) {
            return false;
        }

        //npc.setId(r.readInteger());
        r.readInteger();
        npc.setName(r.readMapleString());
        npc.setMove(r.readBool());
        return true;
    }
}
