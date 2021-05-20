package net.database

import client.Character
import database.jooq.Tables.*
import net.database.DatabaseCore.connection
import net.server.Server
import world.alliance.Alliance
import world.guild.Guild
import world.guild.GuildMark
import world.guild.GuildMember
import world.guild.bbs.BBSComment
import world.guild.bbs.BBSItem
import world.guild.bbs.GuildBBS

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
        connection.insertInto(GUILDMEMBERS, GUILDMEMBERS.GID, GUILDMEMBERS.CID, GUILDMEMBERS.GUILDGRADE)
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
    fun load(id: Int, alliance: Alliance? = null): Guild? {
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

        val allianceId = rec.getValue(GUILDS.ALLIANCEID)
        alliance?.let {
            guild.alliance = it
        } ?: run {
            allianceId?.let {
                with(connection.select().from(ALLIANCES).where(ALLIANCES.ID.eq(allianceId)).fetchOne()) {
                    if (this != null) {
                        val newAlliance = Alliance(allianceId, getValue(ALLIANCES.NAME))
                        newAlliance.notice = getValue(ALLIANCES.NOTICE)
                        newAlliance.maxMemberNum = getValue(ALLIANCES.MAXMEMBERNUM)
                        guild.alliance = newAlliance

                        connection.select().from(GUILDS).where(GUILDS.ALLIANCEID.eq(allianceId))
                            .and(GUILDS.ID.notEqual(id)).fetch().forEach {
                            load(it.getValue(GUILDS.ID), newAlliance)
                        }
                    }
                }
            }
        }

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
        with(GUILDS) {
            connection.update(this)
                .set(NOTICE, guild.notice)
                .set(RANK1, guild.ranks[0])
                .set(RANK2, guild.ranks[1])
                .set(RANK3, guild.ranks[2])
                .set(RANK4, guild.ranks[3])
                .set(RANK5, guild.ranks[4])
                .set(LEADER, guild.leader)
                .execute()
        }
    }

    fun updateMemberGrade(cid: Int, grade: Byte) {
        connection.update(GUILDMEMBERS)
            .set(GUILDMEMBERS.GUILDGRADE, grade)
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

    /**
     * Loads all data, threads and comments, for a specific guild.
     * Should only be used once upon loading the guild
     *
     * @param gid The guildId to load
     * @param bbs The bbs to ref
     */
    fun loadFullBBS(gid: Int, bbs: GuildBBS) {
        val itemRes = connection.select().from(BBSITEMS)
            .where(BBSITEMS.GUILDID.eq(gid))
            .fetch()

        val items = ArrayList<BBSItem>()
        var high = -1
        itemRes.forEach {
            val itemId = it.getValue(BBSITEMS.ITEMID)
            val cid = it.getValue(BBSITEMS.CID)
            val title = it.getValue(BBSITEMS.TITLE)
            val content = it.getValue(BBSITEMS.CONTENT)
            val date = it.getValue(BBSITEMS.DATE)
            val emote = it.getValue(BBSITEMS.EMOTE)

            val commentRes = connection.select().from(BBSCOMMENTS)
                .where(BBSCOMMENTS.GUILDID.eq(gid))
                .and(BBSCOMMENTS.ITEMID.eq(itemId))
                .fetch()

            val comments = ArrayList<BBSComment>()
            var commentHigh = -1
            commentRes.forEach { rec ->
                val commentId = rec.getValue(BBSCOMMENTS.COMMENTID)
                val commentCid = rec.getValue(BBSCOMMENTS.CID)
                val commentDate = rec.getValue(BBSCOMMENTS.DATE)
                val commentContent = rec.getValue(BBSCOMMENTS.CONTENT)

                comments.add(BBSComment(commentId, commentCid, commentDate, commentContent))
                if (commentId > commentHigh) commentHigh = commentId
            }

            val item = BBSItem(itemId, cid, title, content, date, emote, comments)
            items.add(item)
            item.high = commentHigh

            if (itemId > high) high = itemId
        }
        bbs.setItems(items)
        bbs.high = high
    }

    /**
     * Adds a new BBS thread
     */
    fun addBBSItem(gid: Int, item: BBSItem) {
        with(BBSITEMS) {
            connection.insertInto(this, GUILDID, ITEMID, CID, TITLE, CONTENT, DATE, EMOTE)
                .values(gid, item.id, item.cid, item.title, item.content, item.date, item.emote)
                .execute()
        }
    }

    /**
     * Updates an existing BBS thread
     */
    fun updateBBSItem(gid: Int, item: BBSItem) {
        with(BBSITEMS) {
            connection.update(this)
                .set(TITLE, item.title)
                .set(CONTENT, item.content)
                .set(DATE, item.date)
                .set(EMOTE, item.emote)
                .where(GUILDID.eq(gid))
                .and(ITEMID.eq(item.id))
                .execute()
        }
    }

    /**
     * Deletes an existing BBS thread
     */
    fun deleteBBSItem(gid: Int, item: BBSItem) {
        with(BBSITEMS) {
            connection.deleteFrom(this)
                .where(GUILDID.eq(gid))
                .and(ITEMID.eq(item.id))
                .execute()
        }
    }

    /**
     * Adds a new BBS comment on a BBS thread
     */
    fun addBBSComment(gid: Int, iid: Int, comment: BBSComment) {
        with(BBSCOMMENTS) {
            connection.insertInto(this, GUILDID, ITEMID, COMMENTID, CID, DATE, CONTENT)
                .values(gid, iid, comment.id, comment.cid, comment.date, comment.content)
                .execute()
        }
    }

    /**
     * Deletes an existing BBS comment on a BBS thread
     */
    fun deleteBBSComment(gid: Int, iid: Int, comment: BBSComment) {
        with(BBSCOMMENTS) {
            connection.deleteFrom(this)
                .where(GUILDID.eq(gid))
                .and(ITEMID.eq(iid))
                .and(COMMENTID.eq(comment.id))
                .execute()
        }
    }
}