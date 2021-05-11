package world.guild.bbs

import util.packet.PacketWriter

data class BBSComment(
    val id: Int,
    val cid: Int,
    val date: Long,
    val content: String
) {

    fun encode(pw: PacketWriter) {
        pw.writeInt(id) // nEntryID
        pw.writeInt(cid) // nCharacterID
        pw.writeLong(date) // ftDate
        pw.writeMapleString(content)
    }
}