package client.messages.quest

import client.player.quest.QuestState
import util.packet.PacketWriter

class CompleteQuestRecordMessage(questId: Short, private val dateCompleted: Long) : AbstractQuestRecordMessage(questId) {

    override val state: QuestState get() = QuestState.COMPLETE

    override fun encodeData(pw: PacketWriter) {
        super.encodeData(pw)
        pw.writeLong(dateCompleted)
    }
}