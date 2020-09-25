package client.party

import client.Character
import net.maple.packets.PartyPackets
import net.server.Server.getCharacter
import util.packet.Packet
import util.packet.PacketWriter
import java.util.*
import kotlin.collections.ArrayList

class Party(leader: Character) {

    val id: Int
    var leaderId: Int
    private val members: MutableList<PartyMember> = ArrayList()

    companion object {
        var availableId = 1
            private set
    }

    init {
        id = availableId++
        leaderId = leader.id
        addMember(leader)
        println("created party with partyid $id")
    }

    // todo remove?
    fun getMembers(): List<PartyMember> {
        synchronized(members) {
            return ArrayList(members)
        }
    }

    fun addMember(member: Character) {
        synchronized(members) {
            members.add(PartyMember(member))
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

    fun sendMessage(packet: Packet, from: Int) {
        synchronized(members) {
            for (member in members) {
                if (member.isOnline && from != member.cid) {
                    getCharacter(member.cid)?.write(packet.clone())
                }
            }
        }
    }

    fun update() {
        synchronized(members) {
            for (member in getMembers()) {
                if (member.isOnline) {
                    getCharacter(member.cid)?.write(PartyPackets.updateParty(this, member.channel).clone()) // todo don't think clone is needed here
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
                members.remove(toExpel)
            }
        }

        return toExpel
    }

    val onlineMembers: List<PartyMember>
        get() {
            val members: MutableList<PartyMember> = ArrayList()
            synchronized(members) {
                for (member in this.members) {
                    if (member.isOnline) {
                        members.add(member)
                    }
                }
            }
            return members
        }

    // todo check... wtf this is? was i drunk?
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
        members.forEach {
            if (it.channel == memChannel) {
                pw.writeInt(it.field)
            } else {
                pw.writeInt(0)
            }
        } // unsigned int adwFieldID[6]
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
        pw.writeInt(999999999) // dwTownID
        pw.writeInt(999999999) // dwFieldID
        pw.writeInt(0) // nSkillID
        pw.writeInt(0) // tagPOINT m_ptFieldPortal; x
        pw.writeInt(0) // tagPOINT m_ptFieldPortal; y
    }
}
/*
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

 */