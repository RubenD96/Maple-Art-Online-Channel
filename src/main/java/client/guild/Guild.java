package client.guild;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import util.packet.Packet;
import util.packet.PacketWriter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Setter
@Getter
@RequiredArgsConstructor
public class Guild {

    private final int id;
    private int maxSize, leader;
    private String name, notice;
    private final String[] ranks = new String[5];
    private final HashMap<Integer, GuildMember> members = new LinkedHashMap<>();
    private final HashMap<Integer, GuildSkill> skills = new LinkedHashMap<>();
    private GuildMark mark;

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

    public void broadcast(Packet packet) {
        members.values().stream()
                .filter(GuildMember::isOnline)
                .forEach(member -> member.getCharacter().write(packet.clone()));
    }
}
