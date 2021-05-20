package net.maple.packets

import client.Character
import net.database.GuildAPI
import net.maple.SendOpcode
import util.packet.PacketWriter
import world.alliance.Alliance

object AlliancePackets {

    private object AllianceRes {
        const val LOAD_DONE: Byte = 0x0C
        const val LOAD_GUILD_DONE: Byte = 0x0D
        const val NOTIFY_LOGIN_OR_LOGOUT: Byte = 0x0E
        const val CREATE_DONE: Byte = 0x0F
        const val WITHDRAW_DONE: Byte = 0x10
        const val WITHDRAW_FAILED: Byte = 0x11
        const val INVITE_DONE: Byte = 0x12
        const val INVITE_FAILED: Byte = 0x13
        const val INVITE_GUILD_BLOCKED_BY_OPT: Byte = 0x14
        const val INVITE_GUILD_ALREADY_INVITED: Byte = 0x15
        const val INVITE_GUILD_REJECTED: Byte = 0x16
        const val UPDATE_ALLIANCE_INFO: Byte = 0x17
        const val CHANGE_LEVEL_OR_JOB: Byte = 0x18
        const val CHANGE_MASTER_DONE: Byte = 0x19
        const val SET_GRADE_NAME_DONE: Byte = 0x1A
        const val CHANGE_GRADE_DONE: Byte = 0x1B
        const val SET_NOTICE_DONE: Byte = 0x1C
        const val DESTROY_DONE: Byte = 0x1D
        const val UPDATE_GUILD_INFO: Byte = 0x1E
    }

    private fun baseAlliancePacket(op: Byte): PacketWriter {
        val pw = PacketWriter(8)

        pw.writeHeader(SendOpcode.ALLIANCE_RESULT)
        pw.writeByte(op)

        return pw
    }

    fun load(chr: Character, alliance: Alliance?) {
        val pw = baseAlliancePacket(AllianceRes.LOAD_DONE)

        pw.writeBool(alliance != null)
        alliance?.encode(pw)

        chr.write(pw.createPacket())
        alliance?.loadGuilds(chr)
    }

    private fun Alliance.loadGuilds(chr: Character) {
        val pw = baseAlliancePacket(AllianceRes.LOAD_GUILD_DONE)

        pw.writeInt(guilds.size)
        guilds.forEach { it.encode(pw) }

        chr.write(pw.createPacket())
    }

    fun Alliance.notifyLoginOrLogout(chr: Character, online: Boolean) {
        val pw = baseAlliancePacket(AllianceRes.NOTIFY_LOGIN_OR_LOGOUT)

        pw.writeInt(id)
        pw.writeInt(chr.guild!!.id)
        pw.writeInt(chr.id)
        pw.writeBool(online)

        broadcast(pw.createPacket())
    }

    fun Alliance.create() {
        val pw = baseAlliancePacket(AllianceRes.CREATE_DONE)

        encode(pw)
        guilds.forEach { it.encode(pw) }

        broadcast(pw.createPacket())
    }

    fun Alliance.setGradeNames() {
        val pw = baseAlliancePacket(AllianceRes.SET_GRADE_NAME_DONE)

        pw.writeInt(id)
        ranks.forEach { pw.writeMapleString(it) }

        broadcast(pw.createPacket())
    }

    // todo this is not what you think it is
    fun Alliance.changeGrade(name: String) {
        if (name.length < 4 || name.length > 10) return
        val pw = baseAlliancePacket(AllianceRes.CHANGE_GRADE_DONE)
    }

    fun Alliance.setNotice(content: String) {
        if (content.length > 100) return
        val pw = baseAlliancePacket(AllianceRes.SET_NOTICE_DONE)

        pw.writeInt(id)
        pw.writeMapleString(content)

        notice = content
        GuildAPI.updateInfo(this)

        broadcast(pw.createPacket())
    }
}