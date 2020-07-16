package client.player.quest;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import util.packet.IntegerValue;

@RequiredArgsConstructor
public enum QuestRequirementType implements IntegerValue {

    JOB(0x01),
    ITEM(0x02),
    QUEST(0x04),
    MIN_LEVEL(0x08),
    MAX_LEVEL(0x10),
    MOB(0x20),
    NPC(0x40),
    END_DATE(0x80);

    private @NonNull @Getter int value;

    @Override
    public void setValue(int value) {

    }
}