package client.messages

import util.packet.PacketWriter

interface Message {
    val type: MessageType

    fun encode(pw: PacketWriter)
}