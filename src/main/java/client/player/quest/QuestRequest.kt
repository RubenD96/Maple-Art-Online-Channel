package client.player.quest

enum class QuestRequest(val value: Int) {
    LOST_ITEM(0x00),
    ACCEPT_QUEST(0x01),
    COMPLETE_QUEST(0x02),
    RESIGN_QUEST(0x03),
    OPENING_SCRIPT(0x04),
    COMPLETE_SCRIPT(0x05);
}