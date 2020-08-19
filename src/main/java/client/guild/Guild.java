package client.guild;

import lombok.Getter;
import lombok.Setter;
import util.packet.PacketWriter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Setter
@Getter
public class Guild {

    private int id, maxSize;
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
        mark.encode(pw);
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
}
