package client.player.quest;

public enum QuestRequest {

    LOST_ITEM(0x00),
    ACCEPT_QUEST(0x01),
    COMPLETE_QUEST(0x02),
    RESIGN_QUEST(0x03),
    OPENING_SCRIPT(0x04),
    COMPLETE_SCRIPT(0x05);

    private final int value;

    QuestRequest(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}