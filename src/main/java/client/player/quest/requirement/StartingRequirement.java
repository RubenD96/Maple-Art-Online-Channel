package client.player.quest.requirement;

import client.player.quest.QuestRequirementType;
import lombok.Getter;
import util.packet.PacketReader;

import java.util.ArrayList;
import java.util.List;

@Getter
public class StartingRequirement extends Requirement {

    private final List<Short> jobs = new ArrayList<>();
    private int maxLevel;
    private long endDate;

    @Override
    public void decode(int flags, PacketReader reader) {
        super.decode(flags, reader);

        if (containsFlag(flags, QuestRequirementType.JOB)) {
            for (int i = 0; i < reader.readShort(); i++) {
                jobs.add(reader.readShort());
            }
        }
        if (containsFlag(flags, QuestRequirementType.MAX_LEVEL)) maxLevel = reader.readShort();
        if (containsFlag(flags, QuestRequirementType.END_DATE)) endDate = reader.readLong();
    }
}