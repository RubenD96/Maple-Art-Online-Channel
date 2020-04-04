package managers;

import field.object.life.FieldNPC;
import util.packet.PacketReader;

import java.util.HashMap;
import java.util.Map;

public class NPCManager extends AbstractManager {

    private static Map<Integer, FieldNPC> npcs = new HashMap<>();

    public static synchronized FieldNPC getNPC(int id) {
        FieldNPC npc = npcs.get(id);
        if (npc == null) {
            npc = new FieldNPC(id);
            loadNPCData(npc);
            npcs.put(id, npc);
        }
        return npc;
    }

    private static void loadNPCData(FieldNPC npc) {
        PacketReader r = getFieldData("wz/Npc/" + npc.getNpcId() + ".mao");

        //npc.setId(r.readInteger());
        r.readInteger();
        npc.setName(r.readMapleString());
        System.out.println(npc.getName());
        npc.setMove(r.readBool());
    }
}
