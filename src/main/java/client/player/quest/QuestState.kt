package client.player.quest

enum class QuestState(val value: Byte) {
    NONE(0x00),
    PERFORM(0x01),
    COMPLETE(0x02),
    PARTY_QUEST(0x03),
    NO(0x04);
}