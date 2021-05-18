package net.maple.packets

import net.maple.SendOpcode
import util.packet.PacketWriter
import world.alliance.Alliance

object AlliancePackets {

    private object AllianceReq {
        const val Create = 0x0
        const val Load = 0x1
        const val Withdraw = 0x2
        const val Invite = 0x3
        const val Join = 0x4
        const val UpdateMemberCountMax = 0x5
        const val Kick = 0x6
        const val ChangeMaster = 0x7
        const val SetGradeName = 0x8
        const val ChangeGrade = 0x9
        const val SetNotice = 0xA
        const val Destroy = 0xB
    }

    private object AllianceRes {
        const val LoadDone: Byte = 0xC
        const val LoadGuildDone: Byte = 0xD
        const val NotifyLoginOrLogout: Byte = 0xE
        const val CreateDone: Byte = 0xF
        const val Withdraw_Done: Byte = 0x10
        const val Withdraw_Failed: Byte = 0x11
        const val Invite_Done: Byte = 0x12
        const val Invite_Failed: Byte = 0x13
        const val InviteGuild_BlockedByOpt: Byte = 0x14
        const val InviteGuild_AlreadyInvited: Byte = 0x15
        const val InviteGuild_Rejected: Byte = 0x16
        const val UpdateAllianceInfo: Byte = 0x17
        const val ChangeLevelOrJob: Byte = 0x18
        const val ChangeMaster_Done: Byte = 0x19
        const val SetGradeName_Done: Byte = 0x1A
        const val ChangeGrade_Done: Byte = 0x1B
        const val SetNotice_Done: Byte = 0x1C
        const val Destroy_Done: Byte = 0x1D
        const val UpdateGuildInfo: Byte = 0x1E
    }

    private fun baseAlliancePacket(op: Byte): PacketWriter {
        val pw = PacketWriter(8)

        pw.writeHeader(SendOpcode.ALLIANCE_RESULT)
        pw.writeByte(op)

        return pw
    }

    fun create(alliance: Alliance) {
        val pw = baseAlliancePacket(AllianceRes.CreateDone)

        alliance.encode(pw)
        alliance.guilds.forEach { guild -> guild.encode(pw) }

        alliance.broadcast(pw.createPacket())
    }
}