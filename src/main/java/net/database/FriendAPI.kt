package net.database

import client.Character
import database.jooq.Tables
import net.database.DatabaseCore.connection
import net.server.Server

object FriendAPI {

    fun loadFriends(character: Character) {
        val friends = connection.select().from(Tables.FRIENDS)
                .where(Tables.FRIENDS.CID.eq(character.id))
                .fetch()
        val friendList = character.friendList

        friends.forEach {
            val fid = it.getValue(Tables.FRIENDS.FID)
            val friend = friendList.addFriend(fid, CharacterAPI.getOfflineName(fid), it.getValue(Tables.FRIENDS.GROUP))

            val chr = Server.getCharacter(fid) ?: return@forEach
            friend.channel = chr.getChannel().channelId
        }
        friendList.updateFriendList()
        friendList.notifyMutualFriends()
    }

    fun loadPending(character: Character) {
        val pending = connection.select().from(Tables.FRIENDS)
                .where(Tables.FRIENDS.FID.eq(character.id))
                .and(Tables.FRIENDS.PENDING.eq(1.toByte()))
                .fetch()
        val friendList = character.friendList

        pending.forEach {
            val toPend = it.getValue(Tables.FRIENDS.CID)

            if (!friendList.friends.containsKey(toPend)) {
                friendList.pending.add(toPend)
            }
        }
    }

    fun addFriend(cid: Int, fid: Int, group: String, pending: Boolean) {
        connection.insertInto(Tables.FRIENDS,
                Tables.FRIENDS.CID,
                Tables.FRIENDS.FID,
                Tables.FRIENDS.GROUP,
                Tables.FRIENDS.PENDING)
                .values(cid, fid, group, (if (pending) 1 else 0).toByte())
                .execute()
    }

    fun removePendingStatus(cid: Int, fid: Int) {
        connection.update(Tables.FRIENDS)
                .set(Tables.FRIENDS.PENDING, 0.toByte())
                .where(Tables.FRIENDS.CID.eq(cid))
                .and(Tables.FRIENDS.FID.eq(fid))
                .execute()
    }

    fun removeFriend(cid: Int, fid: Int) {
        connection.deleteFrom(Tables.FRIENDS)
                .where(Tables.FRIENDS.CID.eq(cid))
                .and(Tables.FRIENDS.FID.eq(fid))
                .execute()
    }

    fun updateGroup(cid: Int, fid: Int, groupName: String) {
        connection.update(Tables.FRIENDS)
                .set(Tables.FRIENDS.GROUP, groupName)
                .where(Tables.FRIENDS.CID.eq(cid))
                .and(Tables.FRIENDS.FID.eq(fid))
                .execute()
    }
}