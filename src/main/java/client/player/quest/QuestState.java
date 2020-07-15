package client.player.quest;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum QuestState {

    NONE(0x00),
    PERFORM(0x01),
    COMPLETE(0x02),
    PARTY_QUEST(0x03),
    NO(0x04);

    @NonNull @Getter int value;
}
