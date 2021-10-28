package cashshop.misc

import util.packet.PacketWriter

data class NotSale(
    val SN: Int
) {

    fun encode(pw: PacketWriter) {
        pw.writeInt(SN)
    }
}