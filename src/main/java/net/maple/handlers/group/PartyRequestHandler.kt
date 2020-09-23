package net.maple.handlers.group

import client.Character
import client.Client
import client.messages.broadcast.types.AlertMessage
import client.party.Party
import client.party.PartyOperationType
import net.maple.handlers.PacketHandler
import net.maple.packets.CharacterPackets
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
                val invited = c.worldChannel.getCharacter(name)
                        ?: return c.write(CharacterPackets.message(AlertMessage("$name is not present in the current channel.")))

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

/*
package net.maple.handlers.group;

import client.Character;
import client.Client;
import client.messages.broadcast.types.AlertMessage;
import client.party.Party;
import client.party.PartyMember;
import client.party.PartyOperationType;
import net.maple.handlers.PacketHandler;
import net.maple.packets.CharacterPackets;
import net.maple.packets.PartyPackets;
import net.server.Server;
import util.HexTool;
import util.packet.PacketReader;

import java.util.List;

public class PartyRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        Party party = chr.getParty();
        System.out.println("[" + getClass().getName().replace("net.maple.handlers.misc.", "") + "] " + HexTool.INSTANCE.toHex(reader.getData()));
        byte operation = reader.readByte();
        if (operation == PartyOperationType.PARTYREQ_CREATENEWPARTY.getValue()) {
            if (party == null) {
                createParty(chr);
            } else {
                c.write(PartyPackets.INSTANCE.getPartyMessage(PartyOperationType.PARTYRES_CREATENEWPARTY_ALREAYJOINED));
            }
        } else if (operation == PartyOperationType.PARTYREQ_WITHDRAWPARTY.getValue()) {
            if (party != null) {
                int pid = party.getId();
                List<PartyMember> online = party.getOnlineMembers();
                if (party.getMembers().size() == 1 || (online.size() == 1 && chr.getId() == party.getLeaderId())) { // alone, so disband
                    c.write(PartyPackets.INSTANCE.getDisbandPartyPacket(pid, chr.getId()));
                    Server.INSTANCE.getParties().remove(pid);
                } else { // leaving
                    if (online.size() > 1 && chr.getId() == party.getLeaderId()) {
                        PartyMember newLeader = party.getRandomOnline(chr.getId());
                        party.setLeaderId(newLeader.getCid());
                        for (PartyMember pmember : party.getMembers()) {
                            if (pmember.isOnline() && pmember.getCid() != chr.getId()) {
                                Character pm = Server.INSTANCE.getCharacter(pmember.getCid());
                                pm.write(PartyPackets.INSTANCE.getTransferLeaderMessagePacket(newLeader.getCid(), false));
                            }
                        }
                    }
                    c.write(PartyPackets.INSTANCE.getLeavePartyPacket(party, chr.getId(), false, chr.getName(), chr.getChannel().getChannelId()));
                    party.expel(chr.getId());
                    for (PartyMember pmember : party.getMembers()) {
                        if (pmember.isOnline()) {
                            Character pm = Server.INSTANCE.getCharacter(pmember.getCid());
                            pm.write(PartyPackets.INSTANCE.getLeavePartyPacket(party, chr.getId(), false, chr.getName(), pmember.getChannel()));
                        }
                    }
                }
                chr.setParty(null);
            } else {
                c.write(PartyPackets.INSTANCE.getPartyMessage(PartyOperationType.PARTYRES_WITHDRAWPARTY_NOTJOINED));
            }
        } else if (operation == PartyOperationType.PARTYREQ_INVITEPARTY.getValue()) {
            if (party == null) {
                party = createParty(chr);
            }
            if (party.getLeaderId() == chr.getId()) {
                String name = reader.readMapleString();
                Character invited = c.getWorldChannel().getCharacter(name);
                if (invited != null) {
                    if (invited.getParty() == null) {
                        invited.write(PartyPackets.INSTANCE.getSendInvitePacket(chr.getParty().getId(), chr));
                        c.write(PartyPackets.INSTANCE.getPartyMessageExtra(PartyOperationType.PARTYRES_INVITEPARTY_SENT, name));
                    } else {
                        c.write(PartyPackets.INSTANCE.getPartyMessage(PartyOperationType.PARTYRES_CREATENEWPARTY_ALREAYJOINED));
                    }
                } else {
                    c.write(CharacterPackets.message(new AlertMessage(name + " is not present in the current channel.")));
                }
            }
        } else if (operation == PartyOperationType.PARTYREQ_KICKPARTY.getValue()) {
            if (party != null) {
                if (party.getLeaderId() == chr.getId()) {
                    PartyMember toKick = party.expel(reader.readInteger());
                    if (toKick != null) {
                        Character target = Server.INSTANCE.getCharacter(toKick.getCid());
                        if (target != null) {
                            target.setParty(null);
                            target.write(PartyPackets.INSTANCE.getLeavePartyPacket(party, target.getId(), true, target.getName(), toKick.getChannel()));
                        }
                        for (PartyMember pmember : party.getMembers()) {
                            if (pmember.isOnline()) {
                                Character pm = Server.INSTANCE.getCharacter(pmember.getCid());
                                pm.write(PartyPackets.INSTANCE.getLeavePartyPacket(party, toKick.getCid(), true, toKick.getName(), pmember.getChannel()));
                            }
                        }
                    } else {
                        c.write(PartyPackets.INSTANCE.getPartyMessage(PartyOperationType.PARTYRES_KICKPARTY_UNKNOWN));
                    }
                }
            } else {
                c.write(PartyPackets.INSTANCE.getPartyMessage(PartyOperationType.PARTYRES_WITHDRAWPARTY_NOTJOINED));
            }
        } else if (operation == PartyOperationType.PARTYREQ_CHANGEPARTYBOSS.getValue()) {
            if (party != null) {
                int newLeader = reader.readInteger();
                if (party.getLeaderId() == chr.getId()) {
                    PartyMember member = party.getMember(newLeader);
                    if (member.isOnline()) {
                        party.setLeaderId(newLeader);
                        for (PartyMember pmember : party.getMembers()) {
                            if (pmember.isOnline()) {
                                Character pm = Server.INSTANCE.getCharacter(pmember.getCid());
                                pm.write(PartyPackets.INSTANCE.getTransferLeaderMessagePacket(newLeader, false));
                            }
                        }
                    }
                }
            } else {
                c.write(PartyPackets.INSTANCE.getPartyMessage(PartyOperationType.PARTYRES_WITHDRAWPARTY_NOTJOINED));
            }
        }
    }

    public Party createParty(Character chr) {
        Party party = new Party(chr);
        chr.setParty(party);
        Server.INSTANCE.getParties().put(party.getId(), party);
        party.createParty();
        return party;
    }
}
 */