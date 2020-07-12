package client.party;

import client.Character;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.maple.packets.PartyPackets;
import net.server.Server;
import util.packet.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class Party {

    private static int availableId = 1;
    private final @NonNull int id;
    private @Setter int leaderId;
    private final List<PartyMember> members = new ArrayList<>();

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

    /**
     * these 2 update packets are somehow borked
     */
    public void update(Packet packet) {
        for (PartyMember member : members) {
            Character chr = Server.getInstance().getCharacter(member.getCid());
            chr.write(packet);
        }
    }

    public void update(Packet packet, int ignore) {
        for (PartyMember member : members) {
            if (member.getCid() != ignore) {
                Server.getInstance().getCharacter(member.getCid()).write(packet);
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
}
