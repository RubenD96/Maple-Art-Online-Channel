package client.messages.broadcast.types

import client.messages.broadcast.AbstractBroadcastMessage
import client.messages.broadcast.BroadcastMessageType
import util.packet.PacketWriter

/**
 * Color: Pink
 * Prefix:
 */
class EventMessage(private val message: String) : AbstractBroadcastMessage() {

    override val type: BroadcastMessageType get() = BroadcastMessageType.EVENT

    override fun encodeData(pw: PacketWriter) {
        pw.writeMapleString(message)
    }
}