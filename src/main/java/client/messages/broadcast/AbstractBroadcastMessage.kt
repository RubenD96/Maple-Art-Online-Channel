package client.messages.broadcast

import util.packet.PacketWriter

abstract class AbstractBroadcastMessage : BroadcastMessage {

    abstract override val type: BroadcastMessageType

    override fun encode(pw: PacketWriter) {
        pw.write(type.value)
        encodeData(pw)
    }

    protected abstract fun encodeData(pw: PacketWriter)
}