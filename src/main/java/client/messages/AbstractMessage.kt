package client.messages

import util.packet.PacketWriter

abstract class AbstractMessage : Message {

    abstract override val type: MessageType

    override fun encode(pw: PacketWriter) {
        pw.write(type.value)
        encodeData(pw)
    }

    protected abstract fun encodeData(pw: PacketWriter)
}