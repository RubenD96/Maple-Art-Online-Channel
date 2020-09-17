package net.maple.handlers.group

import client.Client
import client.party.PartyOperationType
import net.maple.handlers.PacketHandler
import net.maple.packets.PartyPackets.getServerMsgPacket
import net.server.Server.parties
import util.HexTool.toHex
import util.packet.PacketReader

class PartyResultHandler : PacketHandler() {

    override fun handlePacket(reader: PacketReader, c: Client) {
        println("[" + javaClass.name.replace("net.maple.handlers.misc.", "") + "] " + toHex(reader.data))

        val operation = reader.readByte()
        val pid = reader.readInteger()
        val party = parties[pid] ?: return

        if (operation.toInt() == PartyOperationType.PARTYRES_INVITEPARTY_REJECTED.value) {
            party.leader?.character?.write(getServerMsgPacket(c.character.getName() + " has rejected the invite to the party."))
        } else if (operation.toInt() == PartyOperationType.PARTYRES_INVITEPARTY_ACCEPTED.value) {
            val chr = c.character
            if (chr.party == null) {
                party.addMember(chr)
                chr.party = party
                party.update()
            }
        } else if (operation.toInt() == PartyOperationType.PARTYRES_INVITEPARTY_ALREADYINVITEDBYINVITER.value) {
            party.leader?.character?.write(getServerMsgPacket(c.character.getName() + " is busy."))
        } else if (operation.toInt() == PartyOperationType.PARTYRES_INVITEPARTY_SENT.value) {
            // nothing?
        } else {
            println("[PartyResultHandler] NEW OP: " + toHex(operation))
        }
    }
}

/*
package net.maple.handlers.group;

import client.Character;
import client.Client;
import client.party.Party;
import client.party.PartyOperationType;
import net.maple.handlers.PacketHandler;
import net.maple.packets.PartyPackets;
import net.server.Server;
import util.HexTool;
import util.packet.PacketReader;

public class PartyResultHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        System.out.println("[" + getClass().getName().replace("net.maple.handlers.misc.", "") + "] " + HexTool.INSTANCE.toHex(reader.getData()));
        byte operation = reader.readByte();
        int pid = reader.readInteger();
        Party party = Server.INSTANCE.getParties().get(pid);

        if (party == null) {
            return;
        }

        if (operation == PartyOperationType.PARTYRES_INVITEPARTY_REJECTED.getValue()) {
            party.getLeader().getCharacter().write(PartyPackets.INSTANCE.getServerMsgPacket(c.getCharacter().getName() + " has rejected the invite to the party."));
        } else if (operation == PartyOperationType.PARTYRES_INVITEPARTY_ACCEPTED.getValue()) {
            Character chr = c.getCharacter();
            if (chr.getParty() == null) {
                party.addMember(chr);
                chr.setParty(party);
                party.update();
            }
        } else if (operation == PartyOperationType.PARTYRES_INVITEPARTY_ALREADYINVITEDBYINVITER.getValue()) {
            party.getLeader().getCharacter().write(PartyPackets.INSTANCE.getServerMsgPacket(c.getCharacter().getName() + " is busy."));
        } else if (operation == PartyOperationType.PARTYRES_INVITEPARTY_SENT.getValue()) {
            // nothing?
        } else {
            System.out.println("[PartyResultHandler] NEW OP: " + HexTool.INSTANCE.toHex(operation));
        }
    }
}
 */