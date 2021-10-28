package cashshop.misc

import util.packet.PacketWriter

data class CsStock(
    val SN: Int,
    val stockState: Int
) {

    fun encode(pw: PacketWriter) {
        pw.writeInt(SN)
        pw.writeInt(stockState)
    }
}