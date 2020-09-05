package client.player.quest;

public enum QuestState {

    NONE(0x00),
    PERFORM(0x01),
    COMPLETE(0x02),
    PARTY_QUEST(0x03),
    NO(0x04);

    private final int value;

    QuestState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
