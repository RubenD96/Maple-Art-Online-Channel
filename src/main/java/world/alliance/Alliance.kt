package world.alliance

import util.packet.Packet
import util.packet.PacketWriter
import world.guild.Guild
import java.util.*

class Alliance(val id: Int, val name: String) {

    val ranks: Array<String> = arrayOf("Master", "Jr.Master", "Member", "Member", "Member")
    val guilds = ArrayList<Guild>()
    var notice = ""
    var maxMemberNum = 5

    fun encode(pw: PacketWriter) {
        pw.writeInt(id)
        pw.writeMapleString(name)
        Arrays.stream(ranks).forEach { pw.writeMapleString(it) }
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