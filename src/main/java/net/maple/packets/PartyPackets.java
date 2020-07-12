package net.maple.packets;

import client.Character;
import client.party.Party;
import client.party.PartyMember;
import client.party.PartyOperationType;
import net.maple.SendOpcode;
import util.packet.Packet;
import util.packet.PacketWriter;

import java.util.ArrayList;
import java.util.List;

public class PartyPackets {

    private static PacketWriter getBasePacket(PartyOperationType operation) {
        PacketWriter pw = new PacketWriter(8);

        pw.writeHeader(SendOpcode.PARTY_RESULT);
        pw.write(operation.getValue());

        return pw;
    }

    public static Packet getCreatePartyPacket(int pid) {
        PacketWriter pw = getBasePacket(PartyOperationType.PARTYRES_CREATENEWPARTY_DONE);

        pw.writeInt(pid); // nPartyID
        encodePortal(pw);

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
        encodePartyData(pw, party, memChannel);

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
        encodePartyData(pw, party, memChannel);

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
        encodePartyData(pw, party, memChannel);

        return pw.createPacket();
    }

    public static void encodePartyData(PacketWriter pw, Party party, int memChannel) {
        List<PartyMember> members = new ArrayList<>(party.getMembers());

        while (members.size() < 6) {
            members.add(new PartyMember());
        }

        encodeMembers(pw, members, party.getLeaderId()); // PARTYMEMBER party
        members.forEach(m -> {
            if (m.getChannel() == memChannel) {
                pw.writeInt(m.getField());
            } else {
                pw.writeInt(999999999);
            }
        }); // unsigned int adwFieldID[6]
        members.forEach(m -> encodePortal(pw)); // PARTYDATA::TOWNPORTAL aTownPortal[6]
        members.forEach(m -> pw.writeInt(0)); // int aPQReward
        members.forEach(m -> pw.writeInt(0)); // int aPQRewardType
        pw.writeInt(0); // unsigned int dwPQRewardMobTemplateID
        pw.writeInt(0); // int bPQReward
    }

    public static void encodeMembers(PacketWriter pw, List<PartyMember> members, int bossId) {
        members.forEach(m -> pw.writeInt(m.getCid())); // unsigned int adwCharacterID[6]
        members.forEach(m -> {
            pw.writeString(m.getName(), 13); // char asCharacterName[6][13]
        });
        members.forEach(m -> pw.writeInt(m.getJob())); // int anJob[6]
        members.forEach(m -> pw.writeInt(m.getLevel())); // int anLevel[6]
        members.forEach(m -> pw.writeInt(m.getChannel())); // int anChannelID[6]
        pw.writeInt(bossId); // unsigned int dwPartyBossCharacterID
    }

    public static void encodePortal(PacketWriter pw) {
        pw.writeInt(999999999); // dwTownID
        pw.writeInt(999999999); // dwFieldID
        pw.writeInt(0); // nSkillID
        pw.writeInt(0);
        pw.writeInt(0);
        //pw.writePosition(new Point(0, 0)); // m_ptFieldPortal
    }

    public static Packet getTransferLeaderMessagePacket(int cid, boolean dc) {
        PacketWriter pw = getBasePacket(PartyOperationType.PARTYRES_CHANGEPARTYBOSS_DONE);

        pw.writeInt(cid);
        pw.writeBool(dc);

        return pw.createPacket();
    }
}