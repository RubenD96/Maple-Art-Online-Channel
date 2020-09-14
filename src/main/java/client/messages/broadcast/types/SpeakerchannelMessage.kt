package client.messages.broadcast.types

import client.messages.broadcast.AbstractBroadcastMessage
import client.messages.broadcast.BroadcastMessageType
import util.packet.PacketWriter

/**
 * Color: Dark blue on light blue background
 * Prefix: Medalname charactername
 *
 * Megaphone
 */
class SpeakerchannelMessage(private val message: String, private val medal: String, private val name: String) : AbstractBroadcastMessage() {

    override val type: BroadcastMessageType get() = BroadcastMessageType.SPEAKERCHANNEL

    override fun encodeData(pw: PacketWriter) {
        pw.writeMapleString("$medal$name : $message")
    }
}