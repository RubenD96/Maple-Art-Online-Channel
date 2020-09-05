package world.guild;

import client.Character;
import net.database.CharacterAPI;
import net.server.Server;
import org.jooq.Record;
import util.packet.PacketWriter;

import static database.jooq.Tables.CHARACTERS;
import static database.jooq.Tables.GUILDMEMBERS;

public class GuildMember {

    private String name;
    private int job, level, grade, commitment, allianceGrade;
    private boolean online;
    private Character character;

    /**
     * Used for in-game guild invites
     */
    public GuildMember(Character chr) {
        this.name = chr.getName();
        this.job = chr.getJob().getId();
        this.level = chr.getLevel();
        this.grade = 5;
        this.commitment = 0;
        this.allianceGrade = 5;
        this.online = true;
        this.character = chr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getJob() {
        return job;
    }

    public void setJob(int job) {
        this.job = job;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getCommitment() {
        return commitment;
    }

    public void setCommitment(int commitment) {
        this.commitment = commitment;
    }

    public int getAllianceGrade() {
        return allianceGrade;
    }

    public void setAllianceGrade(int allianceGrade) {
        this.allianceGrade = allianceGrade;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    /**
     * Used for DB load
     *
     * @param rec SQL info
     */
    public GuildMember(Record rec) {
        int id = rec.getValue(GUILDMEMBERS.CID);

        Character chr = Server.Companion.getInstance().getCharacter(id);
        if (chr != null) {
            this.name = chr.getName();
            this.job = chr.getJob().getId();
            this.level = chr.getLevel();
            this.online = true;
            this.character = chr;
        } else {
            Record info = CharacterAPI.INSTANCE.getCharacterInfo(id);
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

    /**
     * For use of method reference
     *
     * @return True if character is set, false if not
     */
    public boolean hasCharacter() {
        return character != null;
    }
}
