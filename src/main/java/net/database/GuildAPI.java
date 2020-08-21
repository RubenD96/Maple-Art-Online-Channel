package net.database;

import client.Character;
import world.guild.Guild;
import world.guild.GuildMark;
import world.guild.GuildMember;
import net.server.Server;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import static database.jooq.Tables.*;

public class GuildAPI {

    /**
     * Creates the guild and returns the new guild id
     *
     * @param guild  Guild to save
     * @param leader Creator of the guild
     * @return Guild id
     */
    public static int create(Guild guild, Character leader) {
        int id = DatabaseCore.getConnection()
                .insertInto(GUILDS, GUILDS.NAME, GUILDS.LEADER)
                .values(guild.getName(), leader.getId())
                .returningResult(GUILDS.ID)
                .fetchOne().value1();

        addMember(guild, leader, true);
        return id;
    }

    /**
     * Inserts a member to the GUILDMEMBERS table
     *
     * @param guild  The guild the member is added to
     * @param member The member to be added
     * @param leader Whether this member is the leader (true for guild creation)
     */
    public static void addMember(Guild guild, Character member, boolean leader) {
        DatabaseCore.getConnection()
                .insertInto(GUILDMEMBERS, GUILDMEMBERS.GID, GUILDMEMBERS.CID, GUILDMEMBERS.GRADE)
                .values(guild.getId(), member.getId(), (byte) (leader ? 1 : 5))
                .execute();
    }

    /**
     * Expel a member based on guild object and characterid
     *
     * @param guild The guild to expel from
     * @param cid   Character id
     */
    public static void expel(Guild guild, int cid) {
        DatabaseCore.getConnection()
                .deleteFrom(GUILDMEMBERS)
                .where(GUILDMEMBERS.GID.eq(guild.getId()))
                .and(GUILDMEMBERS.CID.eq(cid))
                .execute();
    }

    /**
     * Loads all guild information from the database and stores it into java objects
     *
     * @param id id of the guild to load
     * @return Guild, null if not found
     */
    public synchronized static Guild load(int id) {
        if (Server.getInstance().getGuilds().get(id) != null) return Server.getInstance().getGuilds().get(id);
        DSLContext con = DatabaseCore.getConnection();
        Record rec = con.select().from(GUILDS).where(GUILDS.ID.eq(id)).fetchOne();

        if (rec != null) {
            Guild guild = new Guild(id);

            guild.setName(rec.getValue(GUILDS.NAME));
            guild.setNotice(rec.getValue(GUILDS.NOTICE));
            guild.setMaxSize(rec.getValue(GUILDS.SIZE));
            guild.getRanks()[0] = rec.getValue(GUILDS.RANK1);
            guild.getRanks()[1] = rec.getValue(GUILDS.RANK2);
            guild.getRanks()[2] = rec.getValue(GUILDS.RANK3);
            guild.getRanks()[3] = rec.getValue(GUILDS.RANK4);
            guild.getRanks()[4] = rec.getValue(GUILDS.RANK5);
            guild.setLeader(rec.getValue(GUILDS.LEADER));

            Result<Record> res = con.select().from(GUILDMEMBERS).where(GUILDMEMBERS.GID.eq(id)).fetch();
            res.forEach(member -> guild.getMembers().put(member.getValue(GUILDMEMBERS.CID), new GuildMember(member)));

            Record mark = con.select().from(GUILDMARK).where(GUILDMARK.GID.eq(id)).fetchOne();
            if (mark != null) {
                guild.setMark(new GuildMark(mark));
            }

            Server.getInstance().getGuilds().put(id, guild);
            return guild;
        }
        return null;
    }

    /**
     * Removes the guild from the database.
     * Automatically removes mark and members due to foreign keys
     *
     * @param guild The guild to remove
     */
    public static void disband(Guild guild) {
        DatabaseCore.getConnection()
                .deleteFrom(GUILDS)
                .where(GUILDS.ID.eq(guild.getId()))
                .execute();
    }

    /**
     * Update guild info
     *
     * @param guild Guild to update
     */
    public static void updateInfo(Guild guild) {
        DatabaseCore.getConnection()
                .update(GUILDS)
                .set(GUILDS.NOTICE, guild.getNotice())
                .set(GUILDS.RANK1, guild.getRanks()[0])
                .set(GUILDS.RANK2, guild.getRanks()[1])
                .set(GUILDS.RANK3, guild.getRanks()[2])
                .set(GUILDS.RANK4, guild.getRanks()[3])
                .set(GUILDS.RANK5, guild.getRanks()[4])
                .set(GUILDS.LEADER, guild.getLeader())
                .execute();
    }

    public static void updateMemberGrade(int cid, byte grade) {
        DatabaseCore.getConnection()
                .update(GUILDMEMBERS)
                .set(GUILDMEMBERS.GRADE, grade)
                .where(GUILDMEMBERS.CID.eq(cid))
                .execute();
    }

    /**
     * @param chr Character
     * @return Guild id, -1 if no guild was found
     */
    public static int getGuildId(Character chr) {
        var rec = DatabaseCore.getConnection()
                .select(GUILDMEMBERS.GID).from(GUILDMEMBERS)
                .where(GUILDMEMBERS.CID.eq(chr.getId()))
                .fetchOne();
        return rec == null ? -1 : rec.value1();
    }
}
