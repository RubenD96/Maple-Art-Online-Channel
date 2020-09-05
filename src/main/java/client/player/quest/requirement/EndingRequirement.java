package client.player.quest.requirement;

import client.player.quest.QuestRequirementType;
import util.packet.PacketReader;

import java.util.LinkedHashMap;
import java.util.Map;

public class EndingRequirement extends Requirement {

    private final Map<Integer, Short> mobs = new LinkedHashMap<>();

    public Map<Integer, Short> getMobs() {
        return mobs;
    }

    @Override
    public void decode(int flags, PacketReader reader) {
        super.decode(flags, reader);

        if (containsFlag(flags, QuestRequirementType.MOB)) {
            int size = reader.readShort();
            for (int i = 0; i < size; i++) {
                int b = reader.readInteger();
                mobs.put(b, reader.readShort());
            }
        }
    }
}