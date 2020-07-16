package client.player.quest.requirement;

import client.player.quest.QuestRequirementType;
import lombok.Getter;
import util.packet.PacketReader;

import java.util.HashMap;
import java.util.Map;

@Getter
public class EndingRequirement extends Requirement {

    private final Map<Integer, Short> mobs = new HashMap<>();

    @Override
    public void decode(int flags, PacketReader reader) {
        super.decode(flags, reader);

        if (containsFlag(flags, QuestRequirementType.MOB)) {
            for (int i = 0; i < reader.readShort(); i++) {
                mobs.put(reader.readInteger(), reader.readShort());
            }
        }
    }
}