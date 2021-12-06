package net.database

import client.Character
import database.jooq.Tables
import database.jooq.Tables.FRIENDS
import net.database.DatabaseCore.connection
import net.server.Server

object FriendAPI {

    fun loadFriends(character: Character) {
        with(FRIENDS) {
            val friends = connection.select().from(this)
                .where(CID.eq(character.id))
                .fetch()
            val friendList = character.friendList

            friends.forEach {
                val fid = it.getValue(FID)
                val friend =
                    friendList.addFriend(fid, CharacterAPI.getOfflineName(fid), it.getValue(GROUP))

                val chr = Server.getCharacter(fid) ?: return@forEach
                friend.channel = chr.getChannel().channelId
            }
            friendList.updateFriendList()
            friendList.notifyMutualFriends()
        }
    }

    fun loadPending(character: Character) {
        with(FRIENDS) {
            val pending = connection.select().from(this)
                .where(FID.eq(character.id))
                .and(PENDING.eq(1.toByte()))
                .fetch()
            val friendList = character.friendList

            pending.forEach {
                val toPend = it.getValue(CID)

                if (!friendList.friends.containsKey(toPend)) {
                    friendList.pending.add(toPend)
                }
            }
        }
    }

    fun addFriend(cid: Int, fid: Int, group: String, pending: Boolean) {
        with(FRIENDS) {
            connection.insertInto(
                this,
                CID,
                FID,
                GROUP,
                PENDING
            ).values(cid, fid, group, (if (pending) 1 else 0).toByte()).execute()
        }
    }

    fun removePendingStatus(cid: Int, fid: Int) {
        with(FRIENDS) {
            connection.update(this)
                .set(PENDING, 0.toByte())
                .where(CID.eq(cid))
                .and(FID.eq(fid))
                .execute()
        }
    }

    fun removeFriend(cid: Int, fid: Int) {
        with(FRIENDS) {
            connection.deleteFrom(this)
                .where(CID.eq(cid))
                .and(FID.eq(fid))
                .execute()
        }
    }

    fun updateGroup(cid: Int, fid: Int, groupName: String) {
        with(FRIENDS) {
            connection.update(this)
                .set(GROUP, groupName)
                .where(CID.eq(cid))
                .and(FID.eq(fid))
                .execute()
        }
    }
}