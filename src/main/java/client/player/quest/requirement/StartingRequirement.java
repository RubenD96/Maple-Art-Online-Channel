package client.player.quest.requirement;

import client.player.quest.QuestRequirementType;
import util.packet.PacketReader;

import java.util.ArrayList;
import java.util.List;

public class StartingRequirement extends Requirement {

    private final List<Short> jobs = new ArrayList<>();
    private int maxLevel;
    private long endDate;

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public List<Short> getJobs() {
        return jobs;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public long getEndDate() {
        return endDate;
    }

    @Override
    public void decode(int flags, PacketReader reader) {
        super.decode(flags, reader);

        if (containsFlag(flags, QuestRequirementType.JOB)) {
            int size = reader.readShort();
            for (int i = 0; i < size; i++) {
                jobs.add(reader.readShort());
            }
        }
        if (containsFlag(flags, QuestRequirementType.MAX_LEVEL)) maxLevel = reader.readShort();
        if (containsFlag(flags, QuestRequirementType.END_DATE)) endDate = reader.readLong();
    }
}