package client.messages

import util.packet.PacketWriter

class IncMesoMessage(private val meso: Int) : AbstractMessage() {

    override val type: MessageType get() = MessageType.INC_MESO_MESSAGE

    override fun encodeData(pw: PacketWriter) {
        pw.writeInt(meso)
    }
}