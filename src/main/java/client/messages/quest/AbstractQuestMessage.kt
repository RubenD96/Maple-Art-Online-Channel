package client.messages.quest

import client.messages.AbstractMessage
import util.packet.PacketWriter

abstract class AbstractQuestMessage protected constructor(private val questId: Short) : AbstractMessage() {

    override fun encodeData(pw: PacketWriter) {
        pw.writeShort(questId)
    }
}