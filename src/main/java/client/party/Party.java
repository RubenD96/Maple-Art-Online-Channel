package client.party;

import client.Character;
import net.maple.packets.PartyPackets;
import util.packet.Packet;
import util.packet.PacketWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Party {

    private static int availableId = 1;
    private final int id;
    private int leaderId;
    private final List<PartyMember> members = new ArrayList<>();

    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }

    public static int getAvailableId() {
        return availableId;
    }

    public int getId() {
        return id;
    }

    public int getLeaderId() {
        return leaderId;
    }

    public List<PartyMember> getMembers() {
        return members;
    }

    public Party(Character leader) {
        this.id = availableId++;
        this.leaderId = leader.getId();
        addMember(leader);
        System.out.println("created party with partyid " + id);
    }

    public synchronized void addMember(Character member) {
        members.add(new PartyMember(member));
    }

    public synchronized PartyMember getMember(int id) {
        for (PartyMember member : members) {
            if (member.getCid() == id) {
                return member;
            }
        }
        return null;
    }

    public void sendMessage(Packet packet, int from) {
        for (PartyMember member : getMembers()) {
            if (member.isOnline() && from != member.getCid()) {
                member.getCharacter().write(packet.clone());
            }
        }
    }

    public PartyMember getLeader() {
        return getMember(leaderId);
    }

    public synchronized void update() {
        for (PartyMember member : getMembers()) {
            if (member.isOnline()) {
                member.getCharacter().write(PartyPackets.updateParty(this, member.getChannel()).clone());
                member.getCharacter().updatePartyHP(true);
            }
        }
    }

    public synchronized PartyMember expel(int cid) {
        PartyMember toExpel = null;
        for (PartyMember member : members) {
            if (member.getCid() == cid) {
                toExpel = member;
                break;
            }
        }
        if (toExpel != null) {
            members.remove(toExpel);
        }
        return toExpel;
    }

    public synchronized List<PartyMember> getOnlineMembers() {
        List<PartyMember> members = new ArrayList<>();
        for (PartyMember member : this.members) {
            if (member.isOnline()) {
                members.add(member);
            }
        }
        return members;
    }

    public synchronized PartyMember getRandomOnline(int exclude) {
        PartyMember member;
        do {
            member = members.get(new Random().nextInt(members.size()));
        } while (member.getCid() == exclude);
        return member;
    }

    public void encodePartyData(PacketWriter pw, int memChannel) {
        List<PartyMember> members = new ArrayList<>(this.members);

        while (members.size() < 6) {
            members.add(new PartyMember());
        }

        encodeMembers(pw, members, leaderId); // PARTYMEMBER party
        members.forEach(m -> {
            if (m.getChannel() == memChannel) {
                pw.writeInt(m.getField());
            } else {
                pw.writeInt(0);
            }
        }); // unsigned int adwFieldID[6]
        members.forEach(m -> encodePortal(pw)); // PARTYDATA::TOWNPORTAL aTownPortal[6]
        members.forEach(m -> pw.writeInt(0)); // int aPQReward
        members.forEach(m -> pw.writeInt(0)); // int aPQRewardType
        pw.writeInt(0); // unsigned int dwPQRewardMobTemplateID
        pw.writeInt(0); // int bPQReward
    }

    public void encodeMembers(PacketWriter pw, List<PartyMember> members, int bossId) {
        members.forEach(m -> pw.writeInt(m.getCid())); // unsigned int adwCharacterID[6]
        members.forEach(m -> {
            pw.writeFixedString(m.getName(), 13); // char asCharacterName[6][13]
        });
        members.forEach(m -> pw.writeInt(m.getJob())); // int anJob[6]
        members.forEach(m -> pw.writeInt(m.getLevel())); // int anLevel[6]
        members.forEach(m -> pw.writeInt(m.getChannel())); // int anChannelID[6]
        pw.writeInt(bossId); // unsigned int dwPartyBossCharacterID
    }

    public void encodePortal(PacketWriter pw) {
        pw.writeInt(999999999); // dwTownID
        pw.writeInt(999999999); // dwFieldID
        pw.writeInt(0); // nSkillID
        pw.writeInt(0); // tagPOINT m_ptFieldPortal; x
        pw.writeInt(0); // tagPOINT m_ptFieldPortal; y
    }
}
