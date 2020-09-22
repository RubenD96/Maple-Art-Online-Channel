package net.maple.packets

import client.Character
import client.party.Party
import client.party.PartyOperationType
import net.maple.SendOpcode
import util.packet.Packet
import util.packet.PacketWriter

object PartyPackets {

    fun getBasePacket(operation: PartyOperationType): PacketWriter {
        val pw = PacketWriter(8)

        pw.writeHeader(SendOpcode.PARTY_RESULT)
        pw.write(operation.value)

        return pw
    }

    fun getBaseLeavePacket(pid: Int, cid: Int): PacketWriter {
        val pw = getBasePacket(PartyOperationType.PARTYRES_WITHDRAWPARTY_DONE)

        pw.writeInt(pid) // nPartyID
        pw.writeInt(cid) // dwCharacterId

        return pw
    }

    fun Party.getLeavePartyPacket(cid: Int, expel: Boolean, name: String, memChannel: Int): Packet {
        val pw = getBaseLeavePacket(this.id, cid)

        pw.writeBool(true)
        pw.writeBool(expel) // false = leave / true = expel
        pw.writeMapleString(name) // CharacterName
        this.encode(pw, memChannel)

        return pw.createPacket()
    }

    fun getDisbandPartyPacket(pid: Int, cid: Int): Packet {
        val pw = getBaseLeavePacket(pid, cid)

        pw.writeBool(false)

        return pw.createPacket()
    }

    fun getPartyMessage(message: PartyOperationType): Packet {
        return getBasePacket(message).createPacket()
    }

    fun getPartyMessageExtra(message: PartyOperationType, extra: String): Packet {
        val pw = getBasePacket(message)

        pw.writeMapleString(extra)

        return pw.createPacket()
    }

    fun getServerMsgPacket(message: String): Packet {
        val pw = getBasePacket(PartyOperationType.PARTYRES_SERVERMSG)

        pw.write(1)
        pw.writeMapleString(message)

        return pw.createPacket()
    }

    fun getSendInvitePacket(pid: Int, from: Character): Packet {
        val pw = getBasePacket(PartyOperationType.PARTYREQ_INVITEPARTY)

        pw.writeInt(pid)
        pw.writeMapleString(from.name)
        // nexon please, get ur naming in order
        pw.writeInt(from.level) // nSkillID
        pw.writeInt(from.job) // sName
        pw.write(0) // sMsg

        return pw.createPacket()
    }

    fun Party.getJoinPacket(name: String, memChannel: Int): Packet {
        val pw = getBasePacket(PartyOperationType.PARTYRES_JOINPARTY_DONE)

        pw.writeInt(this.id)
        pw.writeMapleString(name)
        this.encode(pw, memChannel)

        return pw.createPacket()
    }

    fun getUpdatePartyHealthPacket(chr: Character): Packet {
        val pw = PacketWriter(14)

        pw.writeHeader(SendOpcode.USER_HP)
        pw.writeInt(chr.id)
        pw.writeInt(chr.health)
        pw.writeInt(chr.trueMaxHealth)

        return pw.createPacket()
    }

    fun updateParty(party: Party, memChannel: Int): Packet {
        val pw = getBasePacket(PartyOperationType.PARTYRES_LOADPARTY_DONE)

        pw.writeInt(party.id)
        party.encode(pw, memChannel)

        return pw.createPacket()
    }

    fun getTransferLeaderMessagePacket(cid: Int, dc: Boolean): Packet {
        val pw = getBasePacket(PartyOperationType.PARTYRES_CHANGEPARTYBOSS_DONE)

        pw.writeInt(cid)
        pw.writeBool(dc)

        return pw.createPacket()
    }
}