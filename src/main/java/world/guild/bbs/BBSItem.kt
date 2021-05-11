package world.guild.bbs

import util.packet.PacketWriter

data class BBSItem(
    val id: Int,
    val cid: Int,
    var title: String,
    var content: String,
    var date: Long,
    var emote: Int,
    private val comments: MutableList<BBSComment> = ArrayList()
) {

    var high = 0

    fun deleteComment(comment: BBSComment) {
        synchronized(comments) {
            comments.remove(comment)
        }
    }

    fun getById(id: Int): BBSComment? {
        synchronized(comments) {
            return comments.firstOrNull { it.id == id }
        }
    }

    fun addComment(cid: Int, content: String): BBSComment {
        synchronized(comments) {
            high++

            val comment = BBSComment(high, cid, System.currentTimeMillis(), content)
            comments.add(comment)

            return comment
        }
    }

    fun encodeSimple(pw: PacketWriter) {
        pw.writeInt(id) // nEntryID
        pw.writeInt(cid) // nCharacterID
        pw.writeMapleString(title) // name
        pw.writeLong(date) // ftDate
        pw.writeInt(emote) // nEmoticon
        pw.writeInt(comments.size) // nComments
    }

    fun encodeDetailed(pw: PacketWriter) {
        pw.writeInt(id)
        pw.writeInt(cid)
        pw.writeLong(date)
        pw.writeMapleString(title)
        pw.writeMapleString(content)
        pw.writeInt(emote)

        pw.writeInt(comments.size)
        comments.forEach { it.encode(pw) }
    }
}