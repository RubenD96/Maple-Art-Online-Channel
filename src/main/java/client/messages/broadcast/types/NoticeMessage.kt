package client.messages.broadcast.types

import client.messages.broadcast.AbstractBroadcastMessage
import client.messages.broadcast.BroadcastMessageType
import util.packet.PacketWriter

/**
 * Color: Blue
 * Prefix: [Notice]
 */
class NoticeMessage(private val message: String) : AbstractBroadcastMessage() {

    override val type: BroadcastMessageType get() = BroadcastMessageType.NOTICE

    override fun encodeData(pw: PacketWriter) {
        pw.writeMapleString(message)
    }
}