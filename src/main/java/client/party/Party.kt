package client.party

import client.Character
import field.Field
import net.maple.packets.PartyPackets
import net.server.Server.getCharacter
import util.logging.LogType
import util.logging.Logger.log
import util.packet.Packet
import util.packet.PacketWriter
import java.util.*

class Party(leader: Character) {

    val id: Int
    var leaderId: Int
    private val members: MutableList<PartyMember> = ArrayList()
    var partyQuest: PartyQuest? = null

    companion object {
        var availableId = 1
            private set
    }

    init {
        id = availableId++
        leaderId = leader.id
        addMember(leader)
        log(LogType.PARTY, "[pid: $id] create", this, leader.client)
    }

    fun getMembers(): List<PartyMember> {
        synchronized(members) {
            return ArrayList(members)
        }
    }

    fun addMember(member: Character) {
        synchronized(members) {
            members.add(PartyMember(member))
            log(LogType.PARTY, "[pid: $id] Add member ($member)", this)
        }
    }

    fun getMember(id: Int): PartyMember? {
        synchronized(members) {
            for (member in members) {
                if (member.cid == id) {
                    return member
                }
            }
        }
        return null
    }

    fun getMembersOnSameField(field: Field): List<PartyMember> {
        return onlineMembers.filter { getCharacter(it.cid)?.field == field }
    }

    fun sendMessage(packet: Packet, from: Int) {
        synchronized(members) {
            for (member in members) {
                if (member.isOnline && from != member.cid) {
                    getCharacter(member.cid)?.write(packet.clone())
                    log(LogType.PARTY, "[pid: $id] sendMessage from $from (${packet.header})", this)
                }
            }
        }
    }

    fun update() {
        synchronized(members) {
            for (member in getMembers()) {
                if (member.isOnline) {
                    getCharacter(member.cid)?.write(
                        PartyPackets.updateParty(this, member.channel).clone()
                    ) // todo don't think clone is needed here
                    getCharacter(member.cid)?.updatePartyHP(true)
                }
            }
        }
    }

    fun expel(cid: Int): PartyMember? {
        var toExpel: PartyMember? = null

        synchronized(members) {
            for (member in members) {
                if (member.cid == cid) {
                    toExpel = member
                    break
                }
            }
            if (toExpel != null) {
                log(LogType.PARTY, "[pid: $id] Kick member (k:${toExpel?.cid}, l:$leaderId)", this)
                members.remove(toExpel)
            }
        }

        return toExpel
    }

    val onlineMembers: List<PartyMember>
        get() {
            val members: MutableList<PartyMember> = ArrayList()
            synchronized(members) {
                this.members.filter { it.isOnline }.forEach {
                    members.add(it)
                }
            }
            return members
        }

    fun getRandomOnline(exclude: Int): PartyMember? {
        var member: PartyMember

        synchronized(members) {
            if (members.isEmpty()) return null
            do {
                member = members[Random().nextInt(members.size)]
            } while (member.cid == exclude)
        }

        return member
    }

    fun createParty() {
        val pw = PartyPackets.getBasePacket(PartyOperationType.PARTYRES_CREATENEWPARTY_DONE)

        pw.writeInt(id) // nPartyID
        encodePortal(pw)

        getCharacter(leaderId)?.write(pw.createPacket())
    }

    fun encode(pw: PacketWriter, memChannel: Int) {
        val members: MutableList<PartyMember> = ArrayList(members)
        while (members.size < 6) {
            members.add(PartyMember())
        }

        encodeMembers(pw, members, leaderId) // PARTYMEMBER party
        members.forEach { pw.writeInt(if (it.channel == memChannel) it.field else -1) } // unsigned int adwFieldID[6]

        repeat(members.size) { encodePortal(pw) } // PARTYDATA::TOWNPORTAL aTownPortal[6]
        repeat(members.size) { pw.writeInt(0) } // int aPQReward
        repeat(members.size) { pw.writeInt(0) } // int aPQRewardType

        pw.writeInt(0) // unsigned int dwPQRewardMobTemplateID
        pw.writeInt(0) // int bPQReward
    }

    private fun encodeMembers(pw: PacketWriter, members: List<PartyMember>, bossId: Int) {
        members.forEach { pw.writeInt(it.cid) } // unsigned int adwCharacterID[6]
        members.forEach { pw.writeFixedString(it.name, 13) } // char asCharacterName[6][13]
        members.forEach { pw.writeInt(it.job) } // int anJob[6]
        members.forEach { pw.writeInt(it.level) } // int anLevel[6]
        members.forEach { pw.writeInt(it.channel) } // int anChannelID[6]
        pw.writeInt(bossId) // unsigned int dwPartyBossCharacterID
    }

    private fun encodePortal(pw: PacketWriter) {
        pw.writeInt(0) // dwTownID
        pw.writeInt(0) // dwFieldID
        pw.writeInt(0) // nSkillID

        pw.writeLong(0)
        pw.writeLong(0)
        // todo long?
        //pw.writeInt(0) // tagPOINT m_ptFieldPortal; x
        //pw.writeInt(0) // tagPOINT m_ptFieldPortal; y
    }
}