package net.database

import client.Character
import database.jooq.Tables.*
import net.database.DatabaseCore.connection
import net.server.Server
import world.guild.Guild
import world.guild.GuildMark
import world.guild.GuildMember

object GuildAPI {

    /**
     * Creates the guild and returns the new guild id
     *
     * @param guild  Guild to save
     * @param leader Creator of the guild
     * @return Guild id
     */
    fun create(guild: Guild, leader: Character): Int {
        val id = connection.insertInto(GUILDS, GUILDS.NAME, GUILDS.LEADER)
                .values(guild.name, leader.id)
                .returningResult(GUILDS.ID)
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
        connection.insertInto(GUILDMEMBERS, GUILDMEMBERS.GID, GUILDMEMBERS.CID, GUILDMEMBERS.GRADE)
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
        connection.deleteFrom(GUILDMEMBERS)
                .where(GUILDMEMBERS.GID.eq(guild.id))
                .and(GUILDMEMBERS.CID.eq(cid))
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
        if (Server.guilds.containsKey(id)) return Server.guilds[id]

        val rec = connection.select().from(GUILDS).where(GUILDS.ID.eq(id)).fetchOne() ?: return null

        val guild = Guild(id)
        guild.name = rec.getValue(GUILDS.NAME)
        guild.notice = rec.getValue(GUILDS.NOTICE)
        guild.maxSize = rec.getValue(GUILDS.SIZE)
        guild.ranks[0] = rec.getValue(GUILDS.RANK1)
        guild.ranks[1] = rec.getValue(GUILDS.RANK2)
        guild.ranks[2] = rec.getValue(GUILDS.RANK3)
        guild.ranks[3] = rec.getValue(GUILDS.RANK4)
        guild.ranks[4] = rec.getValue(GUILDS.RANK5)
        guild.leader = rec.getValue(GUILDS.LEADER)

        val res = connection.select().from(GUILDMEMBERS).where(GUILDMEMBERS.GID.eq(id)).fetch()
        res.forEach {
            guild.members[it.getValue(GUILDMEMBERS.CID)] = GuildMember(it)
        }

        connection.select().from(GUILDMARK).where(GUILDMARK.GID.eq(id)).fetchOne()?.let {
            guild.mark = GuildMark(it)
        }

        Server.guilds[id] = guild
        return guild
    }

    /**
     * Removes the guild from the database.
     * Automatically removes mark and members due to foreign keys
     *
     * @param guild The guild to remove
     */
    fun disband(guild: Guild) {
        connection.deleteFrom(GUILDS)
                .where(GUILDS.ID.eq(guild.id))
                .execute()
    }

    /**
     * Update guild info
     *
     * @param guild Guild to update
     */
    fun updateInfo(guild: Guild) {
        connection.update(GUILDS)
                .set(GUILDS.NOTICE, guild.notice)
                .set(GUILDS.RANK1, guild.ranks[0])
                .set(GUILDS.RANK2, guild.ranks[1])
                .set(GUILDS.RANK3, guild.ranks[2])
                .set(GUILDS.RANK4, guild.ranks[3])
                .set(GUILDS.RANK5, guild.ranks[4])
                .set(GUILDS.LEADER, guild.leader)
                .execute()
    }

    fun updateMemberGrade(cid: Int, grade: Byte) {
        connection.update(GUILDMEMBERS)
                .set(GUILDMEMBERS.GRADE, grade)
                .where(GUILDMEMBERS.CID.eq(cid))
                .execute()
    }

    /**
     * @param chr Character
     * @return Guild id, -1 if no guild was found
     */
    fun getGuildId(chr: Character): Int {
        val rec = connection.select(GUILDMEMBERS.GID).from(GUILDMEMBERS)
                .where(GUILDMEMBERS.CID.eq(chr.id))
                .fetchOne()
        return if (rec == null) -1 else rec.value1()
    }
}