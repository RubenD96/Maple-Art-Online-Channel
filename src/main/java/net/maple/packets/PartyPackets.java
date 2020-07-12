package net.maple.packets;

import client.Character;
import client.party.Party;
import client.party.PartyOperationType;
import net.maple.SendOpcode;
import util.packet.Packet;
import util.packet.PacketWriter;

public class PartyPackets {

    private static PacketWriter getBasePacket(PartyOperationType operation) {
        PacketWriter pw = new PacketWriter(8);

        pw.writeHeader(SendOpcode.PARTY_RESULT);
        pw.write(operation.getValue());

        return pw;
    }

    public static Packet getCreatePartyPacket(Party party) {
        PacketWriter pw = getBasePacket(PartyOperationType.PARTYRES_CREATENEWPARTY_DONE);

        pw.writeInt(party.getId()); // nPartyID
        party.encodePortal(pw);

        return pw.createPacket();
    }

    public static PacketWriter getBaseLeavePacket(int pid, int cid) {
        PacketWriter pw = getBasePacket(PartyOperationType.PARTYRES_WITHDRAWPARTY_DONE);

        pw.writeInt(pid); // nPartyID
        pw.writeInt(cid); // dwCharacterId

        return pw;
    }

    public static Packet getLeavePartyPacket(Party party, int cid, boolean expel, String name, int memChannel) {
        PacketWriter pw = getBaseLeavePacket(party.getId(), cid);

        pw.writeBool(true);
        pw.writeBool(expel); // false = leave / true = expel
        pw.writeMapleString(name); // CharacterName todo
        party.encodePartyData(pw, memChannel);

        return pw.createPacket();
    }

    public static Packet getDisbandPartyPacket(int pid, int cid) {
        PacketWriter pw = getBaseLeavePacket(pid, cid);

        pw.writeBool(false);

        return pw.createPacket();
    }

    public static Packet getPartyMessage(PartyOperationType message) {
        return getBasePacket(message).createPacket();
    }

    public static Packet test(int message) {
        PacketWriter pw = new PacketWriter(8);

        pw.writeHeader(SendOpcode.PARTY_RESULT);
        pw.write(message);

        return pw.createPacket();
    }

    public static Packet getPartyMessageExtra(PartyOperationType message, String extra) {
        PacketWriter pw = getBasePacket(message);

        pw.writeMapleString(extra);

        return pw.createPacket();
    }

    public static Packet getSendInvitePacket(int pid, Character from) {
        PacketWriter pw = getBasePacket(PartyOperationType.PARTYREQ_INVITEPARTY);

        pw.writeInt(pid);
        pw.writeMapleString(from.getName());
        // nexon please, get ur naming in order
        pw.writeInt(from.getLevel()); // nSkillID
        pw.writeInt(from.getJob()); // sName
        pw.write(0); // sMsg

        return pw.createPacket();
    }

    public static Packet getJoinPacket(Party party, String name, int memChannel) {
        PacketWriter pw = getBasePacket(PartyOperationType.PARTYRES_JOINPARTY_DONE);

        pw.writeInt(party.getId());
        pw.writeMapleString(name);
        party.encodePartyData(pw, memChannel);

        return pw.createPacket();
    }

    public static Packet getUpdatePartyHealthPacket(Character chr) {
        PacketWriter pw = new PacketWriter(14);

        pw.writeHeader(SendOpcode.USER_HP);
        pw.writeInt(chr.getId());
        pw.writeInt(chr.getHealth());
        pw.writeInt(chr.getTrueMaxHealth());

        return pw.createPacket();
    }

    public static Packet updateParty(Party party, int memChannel) {
        PacketWriter pw = getBasePacket(PartyOperationType.PARTYRES_LOADPARTY_DONE);

        pw.writeInt(party.getId());
        party.encodePartyData(pw, memChannel);

        return pw.createPacket();
    }

    public static Packet getTransferLeaderMessagePacket(int cid, boolean dc) {
        PacketWriter pw = getBasePacket(PartyOperationType.PARTYRES_CHANGEPARTYBOSS_DONE);

        pw.writeInt(cid);
        pw.writeBool(dc);

        return pw.createPacket();
    }
}