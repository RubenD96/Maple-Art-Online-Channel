package client.messages.quest

import client.player.quest.QuestState
import util.packet.PacketWriter

class ResignQuestRecordMessage(questId: Short, private val completed: Boolean) : AbstractQuestRecordMessage(questId) {

    override val state: QuestState get() = QuestState.NONE

    override fun encodeData(pw: PacketWriter) {
        super.encodeData(pw)
        pw.writeBool(completed)
    }
}