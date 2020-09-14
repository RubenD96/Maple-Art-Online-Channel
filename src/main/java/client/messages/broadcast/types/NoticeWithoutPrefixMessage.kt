package client.messages.broadcast.types

import client.messages.broadcast.AbstractBroadcastMessage
import client.messages.broadcast.BroadcastMessageType
import util.packet.PacketWriter

/**
 * Color: Blue
 * Prefix:
 */
class NoticeWithoutPrefixMessage(private val message: String) : AbstractBroadcastMessage() {

    override val type: BroadcastMessageType get() = BroadcastMessageType.NOTICE_WITHOUT_PREFIX

    override fun encodeData(pw: PacketWriter) {
        pw.writeMapleString(message)
        pw.writeInt(0)
    }
}