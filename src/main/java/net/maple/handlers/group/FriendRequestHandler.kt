package net.maple.handlers.group

import client.Client
import net.database.CharacterAPI.getOfflineId
import net.database.CharacterAPI.getOfflineName
import net.database.FriendAPI.addFriend
import net.database.FriendAPI.removeFriend
import net.database.FriendAPI.removePendingStatus
import net.database.FriendAPI.updateGroup
import net.maple.handlers.PacketHandler
import net.server.Server.getCharacter
import util.packet.PacketReader

class FriendRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val operation = reader.readByte()
        val friendList = c.character.friendList

        if (operation.toInt() == FriendOperation.FRIEND_REQ_SET_FRIEND.value) {
            val name = reader.readMapleString()
            val group = reader.readMapleString()

            if (name.length < 4 || name.length > 12 || group.length > 16) {
                c.close(this, "Invalid name/group on SET friend request ($name/$group)")
                return
            }

            if (name == c.character.name) {
                c.close(this, "Adding yourself as friend")
                return
            }

            val friend = friendList.friends[getOfflineId(name)] ?: return friendList.sendFriendRequest(name, group)

            friend.group = group
            updateGroup(c.character.id, friend.characterId, group)
            friendList.updateFriendList()
        } else if (operation.toInt() == FriendOperation.FRIEND_REQ_ACCEPT_FRIEND.value) {
            val cid = reader.read()
            val toAdd = getCharacter(cid)

            toAdd?.let {
                friendList.addFriend(it, "Group Unknown", true)
                addFriend(c.character.id, it.id, "Group Unknown", false)
                removePendingStatus(it.id, c.character.id)

                it.friendList.friends[c.character.id]?.let { friend ->
                    friend.channel = c.character.getChannel().channelId
                }

                it.friendList.updateFriendList()
            } ?: run {
                val name = getOfflineName(cid)

                if (name != "") {
                    friendList.addFriend(cid, getOfflineName(cid), "Group Unknown")
                    addFriend(c.character.id, cid, "Group Unknown", false)
                    removePendingStatus(cid, c.character.id)
                } else {
                    friendList.sendFriendMessage(FriendOperation.FRIEND_RES_SET_FRIEND_UNKNOWN_USER)
                }
            }

            friendList.updateFriendList()
            friendList.sendPendingRequest()
        } else if (operation.toInt() == FriendOperation.FRIEND_REQ_DELETE_FRIEND.value) {
            val cid = reader.read()

            if (friendList.friends.containsKey(cid)) {
                friendList.removeFriend(cid)
                removeFriend(c.character.id, cid)

                getCharacter(cid)?.friendList?.updateFriendList()

                friendList.updateFriendList()
            } else {
                c.close(this, "Deleting unknown friend")
            }
        }
    }

    enum class FriendOperation(val value: Int) {
        FRIEND_REQ_LOAD_FRIEND(0x0),
        FRIEND_REQ_SET_FRIEND(0x1),
        FRIEND_REQ_ACCEPT_FRIEND(0x2),
        FRIEND_REQ_DELETE_FRIEND(0x3),
        FRIEND_REQ_NOTIFY_LOGIN(0x4),
        FRIEND_REQ_NOTIFY_LOGOUT(0x5),
        FRIEND_REQ_INC_MAX_COUNT(0x6),
        FRIEND_RES_LOAD_FRIEND_DONE(0x7),
        FRIEND_RES_NOTIFY_CHANGE_FRIEND_INFO(0x8),
        FRIEND_RES_INVITE(0x9),
        FRIEND_RES_SET_FRIEND_DONE(0xA),
        FRIEND_RES_SET_FRIEND_FULL_ME(0xB),
        FRIEND_RES_SET_FRIEND_FULL_OTHER(0xC),
        FRIEND_RES_SET_FRIEND_ALREADY_SET(0xD),
        FRIEND_RES_SET_FRIEND_MASTER(0xE),
        FRIEND_RES_SET_FRIEND_UNKNOWN_USER(0xF),
        FRIEND_RES_SET_FRIEND_UNKNOWN(0x10),
        FRIEND_RES_ACCEPT_FRIEND_UNKNOWN(0x11),
        FRIEND_RES_DELETE_FRIEND_DONE(0x12),
        FRIEND_RES_DELETE_FRIEND_UNKNOWN(0x13),
        FRIEND_RES_NOTIFY(0x14),
        FRIEND_RES_INC_MAX_COUNT_DONE(0x15),
        FRIEND_RES_INC_MAX_COUNT_UNKNOWN(0x16),
        FRIEND_RES_PLEASE_WAIT(0x17);
    }
}