package client.messages.broadcast.types

import client.messages.broadcast.AbstractBroadcastMessage
import client.messages.broadcast.BroadcastMessageType
import util.packet.PacketWriter

/**
 * NPC dialogue
 */
class UtilDlgExMessage(private val message: String, private val npc: Int) : AbstractBroadcastMessage() {

    override val type: BroadcastMessageType get() = BroadcastMessageType.UTIL_DLG_EX

    override fun encodeData(pw: PacketWriter) {
        pw.writeMapleString(message)
        pw.writeInt(npc)
    }
}