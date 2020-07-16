package client.player.quest.requirement;

import client.player.quest.QuestRequirementType;
import lombok.Getter;
import lombok.Setter;
import util.packet.PacketReader;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class Requirement {

    @Setter int minLevel, npc;
    Map<Integer, Short> items = new HashMap<>();
    Map<Integer, Byte> quests = new HashMap<>();

    public void decode(int flags, PacketReader reader) {
        if (containsFlag(flags, QuestRequirementType.MIN_LEVEL)) minLevel = reader.readShort();
        if (containsFlag(flags, QuestRequirementType.NPC)) npc = reader.readInteger();
        if (containsFlag(flags, QuestRequirementType.ITEM)) {
            for (int i = 0; i < reader.readShort(); i++) {
                items.put(reader.readInteger(), reader.readShort());
            }
        }
        if (containsFlag(flags, QuestRequirementType.QUEST)) {
            for (int i = 0; i < reader.readShort(); i++) {
                quests.put(reader.readInteger(), reader.readByte());
            }
        }
    }

    public boolean containsFlag(int flags, QuestRequirementType flag) {
        return (flags & flag.getValue()) == flag.getValue();
    }
}