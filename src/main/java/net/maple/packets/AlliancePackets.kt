package net.maple.packets

import client.Character
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
    }

    fun create(alliance: Alliance) {
        val pw = baseAlliancePacket(AllianceRes.CREATE_DONE)

        alliance.encode(pw)
        alliance.guilds.forEach { guild -> guild.encode(pw) }

        alliance.broadcast(pw.createPacket())
    }

    fun setGradeName(alliance: Alliance) {
        val pw = baseAlliancePacket(AllianceRes.SET_GRADE_NAME_DONE)

        pw.writeInt(alliance.id)
        alliance.ranks.forEach { pw.writeMapleString(it) }

        alliance.broadcast(pw.createPacket())
    }
}