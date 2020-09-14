package client.messages.broadcast

import util.packet.PacketWriter

interface BroadcastMessage {
    val type: BroadcastMessageType

    fun encode(pw: PacketWriter)
}