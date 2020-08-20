package client.guild;

import client.Character;
import lombok.Getter;
import lombok.Setter;
import net.database.CharacterAPI;
import net.server.Server;
import org.jooq.Record;
import util.packet.PacketWriter;

import static database.jooq.Tables.CHARACTERS;
import static database.jooq.Tables.GUILDMEMBERS;

@Getter
@Setter
public class GuildMember {

    private String name;
    private int job, level, grade, commitment, allianceGrade;
    private boolean online;
    private Character character;

    public GuildMember(String name, int job, int level, int grade, int commitment, int allianceGrade, boolean online) {
        this.name = name;
        this.job = job;
        this.level = level;
        this.grade = grade;
        this.commitment = commitment;
        this.allianceGrade = allianceGrade;
        this.online = online;
    }

    public GuildMember(Record rec) {
        int id = rec.getValue(GUILDMEMBERS.CID);

        Character chr = Server.getInstance().getCharacter(id);
        if (chr != null) {
            this.name = chr.getName();
            this.job = chr.getJob().getId();
            this.level = chr.getLevel();
            this.online = true;
            this.character = chr;
        } else {
            Record info = CharacterAPI.getCharacterInfo(id);
            this.name = info.getValue(CHARACTERS.NAME);
            this.job = info.getValue(CHARACTERS.JOB);
            this.level = info.getValue(CHARACTERS.LEVEL);
            this.online = false;
        }

        this.grade = rec.getValue(GUILDMEMBERS.GRADE);
        this.commitment = 0;
        this.allianceGrade = 0;
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
