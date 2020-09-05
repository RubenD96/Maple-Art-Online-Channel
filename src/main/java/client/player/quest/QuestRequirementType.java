package client.player.quest;

import util.packet.IntegerValue;

public enum QuestRequirementType implements IntegerValue {

    JOB(0x01),
    ITEM(0x02),
    QUEST(0x04),
    MIN_LEVEL(0x08),
    MAX_LEVEL(0x10),
    MOB(0x20),
    NPC(0x40),
    END_DATE(0x80);

    private final int value;

    QuestRequirementType(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int value) {

    }
}