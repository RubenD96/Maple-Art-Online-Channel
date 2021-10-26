package world.guild

import client.Character
import net.database.GuildAPI
import net.maple.packets.GuildPackets
import net.maple.packets.GuildPackets.setGuildMarkPacket
import net.server.Server
import util.HexTool
import util.logging.LogType
import util.logging.Logger.log
import util.packet.Packet
import util.packet.PacketWriter
import world.alliance.Alliance
import world.guild.bbs.GuildBBS
import java.util.*

class Guild(val id: Int) {

    var maxSize = 0
    var leader = 0
    lateinit var name: String
    lateinit var notice: String
    val ranks = arrayOfNulls<String>(5)
    val members: HashMap<Int, GuildMember> = LinkedHashMap()
    val skills: HashMap<Int, GuildSkill> = LinkedHashMap()
    var mark: GuildMark? = null
    val bbs = GuildBBS(id).also { GuildAPI.loadFullBBS(id, it) }
    var alliance: Alliance? = null
        set(value) {
            field = value
            value?.guilds?.add(this)
        }

    fun encode(pw: PacketWriter) {
        pw.writeInt(id)
        pw.writeMapleString(name)

        Arrays.stream(ranks).forEach { pw.writeMapleString(it ?: "") }

        synchronized(members) {
            pw.write(members.size)
            members.keys.forEach { pw.writeInt(it) }
            members.values.forEach { it.encode(pw) }
        }

        pw.writeInt(maxSize)
        mark?.encode(pw) ?: pw.write(ByteArray(6))
        pw.writeMapleString(notice)

        pw.writeInt(0) // Point
        pw.writeInt(alliance?.id ?: 0) // AllianceID
        pw.write(0) // Level?

        pw.writeShort(skills.size) // skills?
        skills.forEach {
            pw.writeInt(it.key)
            it.value.encode(pw)
        }
    }

    fun broadcast(packet: Packet, ignored: Character? = null) {
        synchronized(members) {
            log(LogType.GUILD, "[gid: $id] broadcast (${HexTool.toHex(packet.header.toByte())})", this, ignored?.client)
            members.values.stream()
                .filter { it.isOnline }
                .filter { it.character !== ignored } // useless filter?
                .forEach {
                    it.character?.write(packet.clone())
                }
        }
    }

    fun addMember(chr: Character) {
        synchronized(members) {
            members[chr.id] = GuildMember(chr)
            log(LogType.GUILD, "[gid: $id] add ($chr)", this)
        }
    }

    fun getMemberSecure(id: Int): GuildMember {
        return members[id] ?: throw NullPointerException("getMemberSecure called on id that doesn't exist")
    }

    fun disband() {
        GuildPackets.removeGuild(this)
        GuildAPI.disband(this)

        synchronized(members) {
            members.values.stream()
                .filter { it.isOnline }
                .forEach {
                    it.character?.guild = null
                }
        }

        GuildAPI.guildNames.remove(name)
        Server.guilds.remove(id)
        members.clear()
    }

    fun changeGuildMark(mark: GuildMark) {
        this.mark = mark
        GuildAPI.updateMark(this)

        broadcast(setGuildMarkPacket())
        synchronized(members) {
            members.values
                .filter { it.isOnline }
                .forEach { member -> member.character?.let { GuildPackets.changeGuildMarkRemote(it, mark) } }
        }
    }
}