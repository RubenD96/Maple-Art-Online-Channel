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
        Party party = Server.Companion.getInstance().getParties().get(pid);

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