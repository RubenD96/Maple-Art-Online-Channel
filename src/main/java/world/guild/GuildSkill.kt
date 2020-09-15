package world.guild

import util.packet.PacketWriter

class GuildSkill(private val level: Short, private val expire: Long, private val name: String) {

    fun encode(pw: PacketWriter) {
        pw.writeShort(level)
        pw.writeLong(expire)
        pw.writeMapleString(name)
    }
}