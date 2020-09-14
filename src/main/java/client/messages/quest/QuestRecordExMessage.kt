package client.messages.quest

import client.messages.MessageType
import util.packet.PacketWriter

class QuestRecordExMessage(questId: Short, private val value: String) : AbstractQuestMessage(questId) {

    override val type: MessageType get() = MessageType.QUEST_RECORD_EX_MESSAGE

    override fun encodeData(pw: PacketWriter) {
        super.encodeData(pw)
        pw.writeMapleString(value)
    }
}