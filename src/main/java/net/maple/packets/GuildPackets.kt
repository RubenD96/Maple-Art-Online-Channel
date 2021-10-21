package net.maple.packets

import client.Character
import net.database.GuildAPI
import net.maple.SendOpcode
import net.maple.packets.AlliancePackets.notifyLoginOrLogout
import util.packet.Packet
import util.packet.PacketWriter
import world.guild.Guild
import world.guild.GuildMark
import java.util.*

object GuildPackets {

    fun changeGuildName(chr: Character, name: String) {
        val pw = PacketWriter(8)

        pw.writeHeader(SendOpcode.USER_GUILD_NAME_CHANGED)
        pw.writeInt(chr.id)
        pw.writeMapleString(name)

        chr.field.broadcast(pw.createPacket(), chr)
    }

    fun inputMark(chr: Character) {
        val pw = PacketWriter(3)

        pw.writeHeader(SendOpcode.GUILD_RESULT)
        pw.writeByte(GuildReq.INPUT_MARK)

        chr.write(pw.createPacket())
    }

    fun Guild.getLoadGuildPacket(): Packet {
        val pw = PacketWriter(32)

        pw.writeHeader(SendOpcode.GUILD_RESULT)
        pw.writeByte(GuildRes.LOAD_GUILD_DONE)
        pw.writeBool(true)
        this.encode(pw)

        return pw.createPacket()
    }

    fun expel(chr: Character) {
        val pw = PacketWriter(4)

        pw.writeHeader(SendOpcode.GUILD_RESULT)
        pw.writeByte(GuildRes.LOAD_GUILD_DONE)
        pw.writeBool(false)

        chr.write(pw.createPacket())
    }

    fun Guild.getJoinGuildPacket(chr: Character): Packet {
        val pw = PacketWriter(32)

        pw.writeHeader(SendOpcode.GUILD_RESULT)
        pw.writeByte(GuildRes.JOIN_GUILD_DONE)
        pw.writeInt(this.id)
        pw.writeInt(chr.id)
        this.getMemberSecure(chr.id).encode(pw)

        return pw.createPacket()
    }

    fun Guild.sendInvite(invited: Character, inviter: Character) {
        val pw = PacketWriter(24)

        pw.writeHeader(SendOpcode.GUILD_RESULT)
        pw.writeByte(GuildReq.INVITE_GUILD)
        pw.writeInt(this.id)
        pw.writeMapleString(inviter.name)
        pw.writeInt(inviter.level)
        pw.writeInt(inviter.job)

        invited.write(pw.createPacket())
    }

    fun message(receiver: Character, message: Byte) {
        val pw = PacketWriter(3)

        pw.writeHeader(SendOpcode.GUILD_RESULT)
        pw.writeByte(message)

        receiver.write(pw.createPacket())
    }

    fun Guild.notifyLoginLogout(chr: Character, online: Boolean) {
        val pw = PacketWriter(12)

        pw.writeHeader(SendOpcode.GUILD_RESULT)
        pw.write(GuildRes.NOTIFY_LOGIN_OR_LOGOUT.toInt())
        pw.writeInt(id)
        pw.writeInt(chr.id)
        pw.writeBool(online)

        broadcast(pw.createPacket(), chr)
        alliance?.notifyLoginOrLogout(chr, online)
    }

    fun Guild.leave(cid: Int, name: String, message: Byte) {
        val pw = PacketWriter(16)

        pw.writeHeader(SendOpcode.GUILD_RESULT)
        pw.write(message.toInt())
        pw.writeInt(this.id)
        pw.writeInt(cid)
        pw.writeMapleString(name)

        this.broadcast(pw.createPacket())
    }

    fun Guild.setNotice(notice: String) {
        val pw = PacketWriter(16)

        pw.writeHeader(SendOpcode.GUILD_RESULT)
        pw.write(GuildRes.SET_NOTICE_DONE.toInt())
        pw.writeInt(this.id)
        pw.writeMapleString(notice)

        this.broadcast(pw.createPacket())
    }

    fun Guild.setMemberGrade(cid: Int, grade: Byte) {
        val pw = PacketWriter(12)

        pw.writeHeader(SendOpcode.GUILD_RESULT)
        pw.writeByte(GuildRes.SET_MEMBER_GRADE_DONE)
        pw.writeInt(this.id)
        pw.writeInt(cid)
        pw.writeByte(grade)

        this.broadcast(pw.createPacket())
    }

    fun Guild.setGradeNames() {
        val pw = PacketWriter(16)

        pw.writeHeader(SendOpcode.GUILD_RESULT)
        pw.writeByte(GuildRes.SET_GRADE_NAME_DONE)
        pw.writeInt(this.id)
        Arrays.stream(this.ranks).forEach { pw.writeMapleString(it ?: "") }

        this.broadcast(pw.createPacket())
    }

    fun removeGuild(guild: Guild) {
        val pw = PacketWriter(7)

        pw.writeHeader(SendOpcode.GUILD_RESULT)
        pw.writeByte(GuildRes.REMOVE_GUILD_DONE)
        pw.writeInt(guild.id)

        guild.broadcast(pw.createPacket())
    }

    fun Guild.increaseMemberSize(newSize: Byte) {
        val pw = PacketWriter(8)

        pw.writeHeader(SendOpcode.GUILD_RESULT)
        pw.writeByte(GuildRes.INC_MAX_MEMBER_NUM_DONE)
        pw.writeInt(id)
        pw.writeByte(newSize)

        broadcast(pw.createPacket())
        GuildAPI.updateMemberSize(this)
    }

    fun Guild.setGuildMarkPacket(): Packet {
        val pw = PacketWriter(13)

        pw.writeHeader(SendOpcode.GUILD_RESULT)
        pw.writeByte(GuildRes.SET_MARK_DONE)
        pw.writeInt(id)
        mark?.encode(pw) ?: pw.write(ByteArray(6))

        return pw.createPacket()
    }

    fun changeGuildMarkRemote(chr: Character, mark: GuildMark?) {
        val pw = PacketWriter(12)

        pw.writeHeader(SendOpcode.USER_GUILD_MARK_CHANGED)
        pw.writeInt(chr.id)

        mark?.encode(pw) ?: pw.write(ByteArray(6))

        chr.field.broadcast(pw.createPacket(), chr)
    }

    object GuildReq {
        const val LOAD_GUILD: Byte = 0x00
        const val INPUT_GUILD_NAME: Byte = 0x01
        const val CHECK_GUILD_NAME: Byte = 0x02
        const val CREATE_GUILD_AGREE: Byte = 0x03
        const val CREATE_NEW_GUILD: Byte = 0x04
        const val INVITE_GUILD: Byte = 0x05
        const val JOIN_GUILD: Byte = 0x06
        const val WITHDRAW_GUILD: Byte = 0x07
        const val KICK_GUILD: Byte = 0x08
        const val REMOVE_GUILD: Byte = 0x09
        const val INC_MAX_MEMBER_NUM: Byte = 0x0A
        const val CHANGE_LEVEL: Byte = 0x0B
        const val CHANGE_JOB: Byte = 0x0C
        const val SET_GRADE_NAME: Byte = 0x0D
        const val SET_MEMBER_GRADE: Byte = 0x0E
        const val SET_MARK: Byte = 0x0F
        const val SET_NOTICE: Byte = 0x10
        const val INPUT_MARK: Byte = 0x11
        const val CHECK_QUEST_WAITING: Byte = 0x12
        const val CHECK_QUEST_WAITING_2: Byte = 0x13
        const val INSERT_QUEST_WAITING: Byte = 0x14
        const val CANCEL_QUEST_WAITING: Byte = 0x15
        const val REMOVE_QUEST_COMPLETE_GUILD: Byte = 0x16
        const val INC_POINT: Byte = 0x17
        const val INC_COMMITMENT: Byte = 0x18
        const val SET_QUEST_TIME: Byte = 0x19
        const val SHOW_GUILD_RANKING: Byte = 0x1A
        const val SET_SKILL: Byte = 0x1B
    }

    object GuildRes {
        const val LOAD_GUILD_DONE: Byte = 0x1C
        const val CHECK_GUILD_NAME_AVAILABLE: Byte = 0x1D
        const val CHECK_GUILD_NAME_ALREADY_USED: Byte = 0x1E
        const val CHECK_GUILD_NAME_UNKNOWN: Byte = 0x1F
        const val CREATE_GUILD_AGREE_REPLY: Byte = 0x20
        const val CREATE_GUILD_AGREE_UNKNOWN: Byte = 0x21
        const val CREATE_NEW_GUILD_DONE: Byte = 0x22
        const val CREATE_NEW_GUILD_ALREAY_JOINED: Byte = 0x23
        const val CREATE_NEW_GUILD_GUILD_NAME_ALREAY_EXIST: Byte = 0x24
        const val CREATE_NEW_GUILD_BEGINNER: Byte = 0x25
        const val CREATE_NEW_GUILD_DISAGREE: Byte = 0x26
        const val CREATE_NEW_GUILD_NOT_FULL_PARTY: Byte = 0x27
        const val CREATE_NEW_GUILD_UNKNOWN: Byte = 0x28
        const val JOIN_GUILD_DONE: Byte = 0x29
        const val JOIN_GUILD_ALREADY_JOINED: Byte = 0x2A
        const val JOIN_GUILD_ALREADY_FULL: Byte = 0x2B
        const val JOIN_GUILD_UNKNOWN_USER: Byte = 0x2C
        const val JOIN_GUILD_UNKNOWN: Byte = 0x2D
        const val WITHDRAW_GUILD_DONE: Byte = 0x2E
        const val WITHDRAW_GUILD_NOT_JOINED: Byte = 0x2F
        const val WITHDRAW_GUILD_UNKNOWN: Byte = 0x30
        const val KICK_GUILD_DONE: Byte = 0x31
        const val KICK_GUILD_NOT_JOINED: Byte = 0x32
        const val KICK_GUILD_UNKNOWN: Byte = 0x33
        const val REMOVE_GUILD_DONE: Byte = 0x34
        const val REMOVE_GUILD_NOT_EXIST: Byte = 0x35
        const val REMOVE_GUILD_UNKNOWN: Byte = 0x36
        const val INVITE_GUILD_BLOCKED_USER: Byte = 0x37
        const val INVITE_GUILD_ALREADY_INVITED: Byte = 0x38
        const val INVITE_GUILD_REJECTED: Byte = 0x39
        const val ADMIN_CANNOT_CREATE: Byte = 0x3A
        const val ADMIN_CANNOT_INVITE: Byte = 0x3B
        const val INC_MAX_MEMBER_NUM_DONE: Byte = 0x3C
        const val INC_MAX_MEMBER_NUM_UNKNOWN: Byte = 0x3D
        const val CHANGE_LEVEL_OR_JOB: Byte = 0x3E
        const val NOTIFY_LOGIN_OR_LOGOUT: Byte = 0x3F
        const val SET_GRADE_NAME_DONE: Byte = 0x40
        const val SET_GRADE_NAME_UNKNOWN: Byte = 0x41
        const val SET_MEMBER_GRADE_DONE: Byte = 0x42
        const val SET_MEMBER_GRADE_UNKNOWN: Byte = 0x43
        const val SET_MEMBER_COMMITMENT_DONE: Byte = 0x44
        const val SET_MARK_DONE: Byte = 0x45
        const val SET_MARK_UNKNOWN: Byte = 0x46
        const val SET_NOTICE_DONE: Byte = 0x47
        const val INSERT_QUEST: Byte = 0x48
        const val NOTICE_QUEST_WAITING_ORDER: Byte = 0x49
        const val SET_GUILD_CAN_ENTER_QUEST: Byte = 0x4A
        const val INC_POINT_DONE: Byte = 0x4B
        const val SHOW_GUILD_RANKING: Byte = 0x4C
        const val GUILD_QUEST_NOT_ENOUGH_USER: Byte = 0x4D
        const val GUILD_QUEST_REGISTER_DISCONNECTED: Byte = 0x4E
        const val GUILD_QUEST_NOTICE_ORDER: Byte = 0x4F
        const val AUTHKEY_UPDATE: Byte = 0x50
        const val SET_SKILL_DONE: Byte = 0x51
        const val SERVER_MSG: Byte = 0x52
    }
}