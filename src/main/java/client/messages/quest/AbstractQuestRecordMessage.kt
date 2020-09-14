package client.messages.quest

import client.messages.MessageType
import client.player.quest.QuestState
import util.packet.PacketWriter

abstract class AbstractQuestRecordMessage protected constructor(questId: Short) : AbstractQuestMessage(questId) {

    override val type: MessageType get() = MessageType.QUEST_RECORD_MESSAGE
    abstract val state: QuestState

    override fun encodeData(pw: PacketWriter) {
        super.encodeData(pw)
        pw.write(state.value.toInt())
    }
}