package client.messages

import util.packet.PacketWriter

class ItemDropPickUpMessage(private val itemId: Int) : AbstractMessage() {

    override val type: MessageType get() = MessageType.DROP_PICK_UP_MESSAGE

    override fun encodeData(pw: PacketWriter) {
        pw.write(2) // item
        pw.writeInt(itemId)
    }
}