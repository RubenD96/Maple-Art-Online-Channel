package world.guild

import database.jooq.Tables
import org.jooq.Record
import util.packet.PacketReader
import util.packet.PacketWriter

class GuildMark {

    val markBg: Short
    val mark: Short
    val markBgColor: Byte
    val markColor: Byte

    constructor(reader: PacketReader) {
        markBg = reader.readShort()
        markBgColor = reader.readByte()
        mark = reader.readShort()
        markColor = reader.readByte()
    }

    constructor(rec: Record) {
        markBg = rec.getValue(Tables.GUILDMARK.MARKBG)
        mark = rec.getValue(Tables.GUILDMARK.MARK)
        markBgColor = rec.getValue(Tables.GUILDMARK.MARKBGCOLOR)
        markColor = rec.getValue(Tables.GUILDMARK.MARKCOLOR)
    }

    constructor(markBg: Short, mark: Short, markBgColor: Byte, markColor: Byte) {
        this.markBg = markBg
        this.mark = mark
        this.markBgColor = markBgColor
        this.markColor = markColor
    }

    fun encode(pw: PacketWriter) {
        pw.writeShort(markBg)
        pw.write(markBgColor.toInt())
        pw.writeShort(mark)
        pw.write(markColor.toInt())
    }
}