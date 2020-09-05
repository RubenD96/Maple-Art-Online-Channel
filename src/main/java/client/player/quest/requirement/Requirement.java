package client.player.quest.requirement;

import client.player.quest.QuestRequirementType;
import util.packet.PacketReader;

import java.util.HashMap;
import java.util.Map;

public abstract class Requirement {

    private int minLevel, npc;
    private final Map<Integer, Short> items = new HashMap<>();
    private final Map<Integer, Byte> quests = new HashMap<>();

    public int getMinLevel() {
        return minLevel;
    }

    public int getNpc() {
        return npc;
    }

    public Map<Integer, Short> getItems() {
        return items;
    }

    public Map<Integer, Byte> getQuests() {
        return quests;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public void setNpc(int npc) {
        this.npc = npc;
    }

    public void decode(int flags, PacketReader reader) {
        if (containsFlag(flags, QuestRequirementType.MIN_LEVEL)) minLevel = reader.readShort();
        if (containsFlag(flags, QuestRequirementType.NPC)) npc = reader.readInteger();
        if (containsFlag(flags, QuestRequirementType.ITEM)) {
            int size = reader.readShort();
            for (int i = 0; i < size; i++) {
                items.put(reader.readInteger(), reader.readShort());
            }
        }
        if (containsFlag(flags, QuestRequirementType.QUEST)) {
            int size = reader.readShort();
            for (int i = 0; i < size; i++) {
                quests.put(reader.readInteger(), reader.readByte());
            }
        }
    }

    public boolean containsFlag(int flags, QuestRequirementType flag) {
        return (flags & flag.getValue()) == flag.getValue();
    }
}