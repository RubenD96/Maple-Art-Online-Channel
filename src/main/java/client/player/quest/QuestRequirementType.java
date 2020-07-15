package client.player.quest;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum QuestRequirementType {

    JOB(0x00),
    ITEM(0x01),
    QUEST(0x02),
    MIN_LEVEL(0x03),
    MAX_LEVEL(0x04),
    MOB(0x05),
    NPC(0x06),
    MESO(0x07);

    private @NonNull @Getter int value;
}
