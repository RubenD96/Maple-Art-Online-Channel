package client.messages.broadcast.types

import client.messages.broadcast.AbstractBroadcastMessage
import client.messages.broadcast.BroadcastMessageType
import util.packet.PacketWriter

/**
 * Color: Yellow
 * Prefix:
 *
 * Scrolling message on top
 */
class SlideMessage(private val message: String) : AbstractBroadcastMessage() {

    override val type: BroadcastMessageType get() = BroadcastMessageType.SLIDE

    override fun encodeData(pw: PacketWriter) {
        pw.writeBool(true)
        pw.writeMapleString(message)
    }
}