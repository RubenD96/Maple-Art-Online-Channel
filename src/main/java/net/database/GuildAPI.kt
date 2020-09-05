package net.database

import client.Character
import database.jooq.Tables
import net.database.DatabaseCore.connection
import net.server.Server
import org.jooq.Record
import world.guild.Guild
import world.guild.GuildMark
import world.guild.GuildMember
import java.util.function.Consumer

object GuildAPI {
    /**
     * Creates the guild and returns the new guild id
     *
     * @param guild  Guild to save
     * @param leader Creator of the guild
     * @return Guild id
     */
    fun create(guild: Guild, leader: Character): Int {
        val id = connection
                .insertInto(Tables.GUILDS, Tables.GUILDS.NAME, Tables.GUILDS.LEADER)
                .values(guild.name, leader.id)
                .returningResult(Tables.GUILDS.ID)
                .fetchOne().value1()
        addMember(guild, leader, true)
        return id
    }

    /**
     * Inserts a member to the GUILDMEMBERS table
     *
     * @param guild  The guild the member is added to
     * @param member The member to be added
     * @param leader Whether this member is the leader (true for guild creation)
     */
    fun addMember(guild: Guild, member: Character, leader: Boolean) {
        connection
                .insertInto(Tables.GUILDMEMBERS, Tables.GUILDMEMBERS.GID, Tables.GUILDMEMBERS.CID, Tables.GUILDMEMBERS.GRADE)
                .values(guild.id, member.id, (if (leader) 1 else 5).toByte())
                .execute()
    }

    /**
     * Expel a member based on guild object and characterid
     *
     * @param guild The guild to expel from
     * @param cid   Character id
     */
    fun expel(guild: Guild, cid: Int) {
        connection
                .deleteFrom(Tables.GUILDMEMBERS)
                .where(Tables.GUILDMEMBERS.GID.eq(guild.id))
                .and(Tables.GUILDMEMBERS.CID.eq(cid))
                .execute()
    }

    /**
     * Loads all guild information from the database and stores it into java objects
     *
     * @param id id of the guild to load
     * @return Guild, null if not found
     */
    @Synchronized
    fun load(id: Int): Guild? {
        if (Server.instance.guilds.containsKey(id)) return Server.instance.guilds[id]
        val rec = connection.select().from(Tables.GUILDS).where(Tables.GUILDS.ID.eq(id)).fetchOne()
        if (rec != null) {
            val guild = Guild(id)
            guild.name = rec.getValue(Tables.GUILDS.NAME)
            guild.notice = rec.getValue(Tables.GUILDS.NOTICE)
            guild.maxSize = rec.getValue(Tables.GUILDS.SIZE)
            guild.ranks[0] = rec.getValue(Tables.GUILDS.RANK1)
            guild.ranks[1] = rec.getValue(Tables.GUILDS.RANK2)
            guild.ranks[2] = rec.getValue(Tables.GUILDS.RANK3)
            guild.ranks[3] = rec.getValue(Tables.GUILDS.RANK4)
            guild.ranks[4] = rec.getValue(Tables.GUILDS.RANK5)
            guild.leader = rec.getValue(Tables.GUILDS.LEADER)
            val res = connection.select().from(Tables.GUILDMEMBERS).where(Tables.GUILDMEMBERS.GID.eq(id)).fetch()
            res.forEach(Consumer { member: Record -> guild.members[member.getValue(Tables.GUILDMEMBERS.CID)] = GuildMember(member) })
            val mark = connection.select().from(Tables.GUILDMARK).where(Tables.GUILDMARK.GID.eq(id)).fetchOne()
            if (mark != null) {
                guild.mark = GuildMark(mark)
            }
            Server.instance.guilds[id] = guild
            return guild
        }
        return null
    }

    /**
     * Removes the guild from the database.
     * Automatically removes mark and members due to foreign keys
     *
     * @param guild The guild to remove
     */
    fun disband(guild: Guild) {
        connection
                .deleteFrom(Tables.GUILDS)
                .where(Tables.GUILDS.ID.eq(guild.id))
                .execute()
    }

    /**
     * Update guild info
     *
     * @param guild Guild to update
     */
    fun updateInfo(guild: Guild) {
        connection
                .update(Tables.GUILDS)
                .set(Tables.GUILDS.NOTICE, guild.notice)
                .set(Tables.GUILDS.RANK1, guild.ranks[0])
                .set(Tables.GUILDS.RANK2, guild.ranks[1])
                .set(Tables.GUILDS.RANK3, guild.ranks[2])
                .set(Tables.GUILDS.RANK4, guild.ranks[3])
                .set(Tables.GUILDS.RANK5, guild.ranks[4])
                .set(Tables.GUILDS.LEADER, guild.leader)
                .execute()
    }

    fun updateMemberGrade(cid: Int, grade: Byte) {
        connection
                .update(Tables.GUILDMEMBERS)
                .set(Tables.GUILDMEMBERS.GRADE, grade)
                .where(Tables.GUILDMEMBERS.CID.eq(cid))
                .execute()
    }

    /**
     * @param chr Character
     * @return Guild id, -1 if no guild was found
     */
    fun getGuildId(chr: Character): Int {
        val rec = connection
                .select(Tables.GUILDMEMBERS.GID).from(Tables.GUILDMEMBERS)
                .where(Tables.GUILDMEMBERS.CID.eq(chr.id))
                .fetchOne()
        return if (rec == null) -1 else rec.value1()
    }
}