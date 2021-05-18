package world.alliance

import util.packet.Packet
import util.packet.PacketWriter
import world.guild.Guild
import java.util.*

class Alliance(val id: Int, val name: String) {

    val ranks = arrayOfNulls<String>(5)
    val guilds = ArrayList<Guild>()
    val notice = ""
    val maxMemberNum = 3

    fun encode(pw: PacketWriter) {
        pw.writeInt(id)
        pw.writeMapleString(name)
        Arrays.stream(ranks).forEach { pw.writeMapleString(it ?: "Testing") }
        pw.write(guilds.size)
        guilds.forEach { pw.writeInt(it.id) }
        pw.writeInt(maxMemberNum)
        pw.writeMapleString(notice)
    }

    fun broadcast(packet: Packet) {
        guilds.forEach {
            it.broadcast(packet)
        }
    }
}