package world.guild;

import client.Character;
import util.packet.Packet;
import util.packet.PacketWriter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Guild {

    private final int id;
    private int maxSize, leader;
    private String name, notice;
    private final String[] ranks = new String[5];
    private final HashMap<Integer, GuildMember> members = new LinkedHashMap<>();
    private final HashMap<Integer, GuildSkill> skills = new LinkedHashMap<>();
    private GuildMark mark;

    public Guild(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getLeader() {
        return leader;
    }

    public void setLeader(int leader) {
        this.leader = leader;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String[] getRanks() {
        return ranks;
    }

    public HashMap<Integer, GuildMember> getMembers() {
        return members;
    }

    public HashMap<Integer, GuildSkill> getSkills() {
        return skills;
    }

    public GuildMark getMark() {
        return mark;
    }

    public void setMark(GuildMark mark) {
        this.mark = mark;
    }

    public void encode(PacketWriter pw) {
        pw.writeInt(id);
        pw.writeMapleString(name);

        Arrays.stream(ranks).forEach(pw::writeMapleString);

        pw.write(members.size());
        members.keySet().forEach(pw::writeInt);
        members.values().forEach(member -> member.encode(pw));

        pw.writeInt(maxSize);
        if (mark != null) {
            mark.encode(pw);
        } else {
            pw.write(new byte[6]);
        }
        pw.writeMapleString(notice);

        pw.writeInt(0); // Point
        pw.writeInt(0); // AllianceID
        pw.write(0); // Level?

        pw.writeShort(skills.size()); // skills?
        skills.forEach((k, v) -> {
            pw.writeInt(k);
            v.encode(pw);
        });
    }

    public synchronized void broadcast(Packet packet) {
        broadcast(packet, null);
    }

    public synchronized void broadcast(Packet packet, Character ignored) {
        members.values().stream()
                .filter(GuildMember::isOnline)
                .filter(GuildMember::hasCharacter)
                .filter(member -> member.getCharacter() != ignored)
                .forEach(member -> member.getCharacter().write(packet.clone()));
    }

    public synchronized void addMember(Character chr) {
        members.put(chr.getId(), new GuildMember(chr));
    }
}
