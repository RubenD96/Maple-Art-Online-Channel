package client.messages.broadcast.types

import client.messages.broadcast.AbstractBroadcastMessage
import client.messages.broadcast.BroadcastMessageType
import util.packet.PacketWriter

/**
 * Color: Purple on pink bg
 * Prefix: Medalname charactername ch01
 *
 * Super Megaphone
 */
class SpeakerworldMessage(private val message: String, private val medal: String, private val name: String, channel: Int, private val ear: Boolean) : AbstractBroadcastMessage() {

    private val channel: Byte = channel.toByte()

    override val type: BroadcastMessageType get() = BroadcastMessageType.SPEAKERWORLD

    override fun encodeData(pw: PacketWriter) {
        pw.writeMapleString("$medal$name : $message")
        pw.write(channel.toInt())
        pw.writeBool(ear)
    }
}