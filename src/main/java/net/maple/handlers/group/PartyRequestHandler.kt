package net.maple.handlers.group

import client.Character
import client.Client
import client.messages.broadcast.types.AlertMessage
import client.party.Party
import client.party.PartyOperationType
import net.maple.handlers.PacketHandler
import net.maple.packets.CharacterPackets.message
import net.maple.packets.PartyPackets.getDisbandPartyPacket
import net.maple.packets.PartyPackets.getLeavePartyPacket
import net.maple.packets.PartyPackets.getPartyMessage
import net.maple.packets.PartyPackets.getPartyMessageExtra
import net.maple.packets.PartyPackets.getSendInvitePacket
import net.maple.packets.PartyPackets.getTransferLeaderMessagePacket
import net.server.Server.getCharacter
import net.server.Server.parties
import util.HexTool.toHex
import util.packet.PacketReader

class PartyRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character
        var party = chr.party
        println("[" + javaClass.name.replace("net.maple.handlers.misc.", "") + "] " + toHex(reader.data))

        val operation = reader.readByte()
        if (operation.toInt() == PartyOperationType.PARTYREQ_CREATENEWPARTY.value) {
            party?.let { c.write(getPartyMessage(PartyOperationType.PARTYRES_CREATENEWPARTY_ALREAYJOINED)) }
                    ?: createParty(chr)
        } else if (operation.toInt() == PartyOperationType.PARTYREQ_WITHDRAWPARTY.value) {
            party?.let {
                val pid = it.id
                val online = it.onlineMembers
                if (it.getMembers().size == 1 || online.size == 1 && chr.id == it.leaderId) { // alone, so disband
                    c.write(getDisbandPartyPacket(pid, chr.id))
                    parties.remove(pid)
                } else { // leaving
                    if (online.size > 1 && chr.id == it.leaderId) {
                        val newLeader = it.getRandomOnline(chr.id) // todo disband
                        it.leaderId = newLeader!!.cid
                        for (pmember in it.getMembers()) {
                            if (pmember.isOnline && pmember.cid != chr.id) {
                                val pm = getCharacter(pmember.cid) ?: continue
                                pm.write(getTransferLeaderMessagePacket(newLeader.cid, false))
                            }
                        }
                    }

                    c.write(it.getLeavePartyPacket(chr.id, false, chr.name, chr.getChannel().channelId))
                    it.expel(chr.id)
                    for (pmember in it.getMembers()) {
                        if (pmember.isOnline) {
                            val pm = getCharacter(pmember.cid) ?: continue
                            pm.write(it.getLeavePartyPacket(chr.id, false, chr.name, pmember.channel))
                        }
                    }
                }
                chr.party = null
            } ?: c.write(getPartyMessage(PartyOperationType.PARTYRES_WITHDRAWPARTY_NOTJOINED))
        } else if (operation.toInt() == PartyOperationType.PARTYREQ_INVITEPARTY.value) {
            val unmutableParty = party ?: createParty(chr)

            if (unmutableParty.leaderId == chr.id) {
                val name = reader.readMapleString()
                val invited = getCharacter(name)
                        ?: return chr.message(AlertMessage("$name is not present in any channel."))
                if (invited.getChannel() !== c.worldChannel) return chr.message(AlertMessage("$name is not present in the current channel."))

                invited.party?.let {
                    c.write(getPartyMessage(PartyOperationType.PARTYRES_CREATENEWPARTY_ALREAYJOINED))
                } ?: run {
                    invited.write(getSendInvitePacket(unmutableParty.id, chr))
                    c.write(getPartyMessageExtra(PartyOperationType.PARTYRES_INVITEPARTY_SENT, name))
                }
            }
        } else if (operation.toInt() == PartyOperationType.PARTYREQ_KICKPARTY.value) {
            val unmutableParty = party
                    ?: return c.write(getPartyMessage(PartyOperationType.PARTYRES_WITHDRAWPARTY_NOTJOINED))

            if (unmutableParty.leaderId == chr.id) {
                val toKick = unmutableParty.expel(reader.readInteger())
                        ?: return c.write(getPartyMessage(PartyOperationType.PARTYRES_KICKPARTY_UNKNOWN))

                getCharacter(toKick.cid)?.let {
                    it.party = null
                    it.write(unmutableParty.getLeavePartyPacket(it.id, true, it.name, toKick.channel))
                }

                for (pmember in unmutableParty.getMembers()) {
                    if (pmember.isOnline) {
                        val pm = getCharacter(pmember.cid) ?: continue
                        pm.write(unmutableParty.getLeavePartyPacket(toKick.cid, true, toKick.name, pmember.channel))
                    }
                }
            }
        } else if (operation.toInt() == PartyOperationType.PARTYREQ_CHANGEPARTYBOSS.value) {
            party = party ?: return c.write(getPartyMessage(PartyOperationType.PARTYRES_WITHDRAWPARTY_NOTJOINED))

            val newLeader = reader.readInteger()

            if (party.leaderId == chr.id) {
                val member = party.getMember(newLeader) ?: return

                if (member.isOnline) {
                    party.leaderId = newLeader
                    for (pmember in party.getMembers()) {
                        if (pmember.isOnline) {
                            val pm = getCharacter(pmember.cid) ?: continue
                            pm.write(getTransferLeaderMessagePacket(newLeader, false))
                        }
                    }
                }
            }
        }
    }

    fun createParty(chr: Character): Party {
        val party = Party(chr)
        chr.party = party
        parties[party.id] = party
        party.createParty()
        return party
    }
}