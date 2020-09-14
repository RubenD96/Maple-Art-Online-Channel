package client.messages

import util.packet.PacketWriter

class MesoDropPickUpMessage(private val meso: Int) : AbstractMessage() {

    var isFailed = false
    var premiumIPMesoBonus: Short = 0 // wtf?

    override val type: MessageType get() = MessageType.DROP_PICK_UP_MESSAGE

    override fun encodeData(pw: PacketWriter) {
        pw.write(1) // meso
        pw.writeBool(isFailed)
        pw.writeInt(meso)
        pw.writeShort(premiumIPMesoBonus)
    }
}