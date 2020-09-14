package client.messages.quest

import client.player.quest.QuestState
import util.packet.PacketWriter

class PerformQuestRecordMessage(questId: Short, private val value: String) : AbstractQuestRecordMessage(questId) {

    override val state: QuestState get() = QuestState.PERFORM

    override fun encodeData(pw: PacketWriter) {
        super.encodeData(pw)
        pw.writeMapleString(value)
    }
}