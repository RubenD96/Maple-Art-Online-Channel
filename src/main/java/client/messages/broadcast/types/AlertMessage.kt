package client.messages.broadcast.types

import client.messages.broadcast.AbstractBroadcastMessage
import client.messages.broadcast.BroadcastMessageType
import util.packet.PacketWriter

/**
 * Color:
 * Prefix:
 *
 * Alert popup
 */
class AlertMessage(private val message: String) : AbstractBroadcastMessage() {

    override val type: BroadcastMessageType get() = BroadcastMessageType.ALERT

    override fun encodeData(pw: PacketWriter) {
        pw.writeMapleString(message)
    }
}