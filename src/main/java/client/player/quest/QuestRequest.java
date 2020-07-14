package client.player.quest;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum QuestRequest {

    LOST_ITEM(0x00),
    ACCEPT_QUEST(0x01),
    COMPLETE_QUEST(0x02),
    RESIGN_QUEST(0x03),
    OPENING_SCRIPT(0x04),
    COMPLETE_SCRIPT(0x05);

    @NonNull @Getter int value;
}