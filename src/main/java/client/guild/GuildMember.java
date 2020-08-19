package client.guild;

import util.packet.PacketWriter;

public class GuildMember {

    private String name;
    private int job, level, grade, commitment, allianceGrade;
    private boolean online;

    public GuildMember(String name, int job, int level, int grade, int commitment, int allianceGrade, boolean online) {
        this.name = name;
        this.job = job;
        this.level = level;
        this.grade = grade;
        this.commitment = commitment;
        this.allianceGrade = allianceGrade;
        this.online = online;
    }

    public void encode(PacketWriter pw) {
        pw.writeFixedString(name, 13); // 13
        pw.writeInt(job); // 17
        pw.writeInt(level); // 21
        pw.writeInt(grade); // 25
        pw.writeInt(online ? 1 : 0); // 29
        pw.writeInt(commitment); // 33
        pw.writeInt(allianceGrade); // 37
    }
}
