package client.player.friend

import client.Character
import database.jooq.Tables
import net.database.CharacterAPI.getOfflineCharacter
import net.database.CharacterAPI.getOfflineId
import net.database.FriendAPI.addFriend
import net.maple.SendOpcode
import net.maple.handlers.group.FriendRequestHandler.FriendOperation
import net.server.Server.getCharacter
import util.packet.Packet
import util.packet.PacketWriter
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.collections.LinkedHashMap

class FriendList(private val owner: Character) {

    val friends: MutableMap<Int, Friend> = LinkedHashMap()
    val pending: Queue<Int> = LinkedBlockingQueue()

    fun addFriend(friend: Character, group: String, online: Boolean): Friend {
        return addFriend(friend.id, friend.getChannel().channelId, friend.name, group, online)
    }

    fun addFriend(id: Int, name: String, group: String): Friend {
        return addFriend(id, -1, name, group, false)
    }

    fun addFriend(id: Int, channel: Int, name: String, group: String, online: Boolean): Friend {
        synchronized(friends) {
            val friend = Friend(id, channel, name, group, online)
            friends[id] = friend
            return friend
        }
    }

    fun removeFriend(id: Int) {
        synchronized(friends) {
            friends.remove(id)
        }
    }

    fun sendMessage(packet: Packet) {
        synchronized(friends) {
            friends.keys.forEach {
                getCharacter(it)?.let { chr ->
                    chr.friendList.friends[owner.id]?.run {
                        chr.write(packet.clone())
                    }
                }
            }
        }
    }

    private val updateFriendsPacket: Packet
        get() {
            val pw = PacketWriter(8)
            pw.writeHeader(SendOpcode.FRIEND_RESULT)
            pw.write(FriendOperation.FRIEND_RES_LOAD_FRIEND_DONE.value)

            synchronized(friends) {
                pw.write(friends.size)

                for (f in friends.values) {
                    val visible = f.isOnline() && getCharacter(f.characterId)!!.friendList.friends.containsKey(owner.id)
                    encodeGWFriend(pw, f.group, f.characterId, f.name, if (visible) 0 else 2, f.channel)
                }

                repeat(friends.count()) { pw.writeInt(0) }
            }

            return pw.createPacket()
        }

    private fun getFriendChannelChangePacket(cid: Int, channel: Int): Packet {
        val pw = PacketWriter(12)

        pw.writeHeader(SendOpcode.FRIEND_RESULT)
        pw.write(FriendOperation.FRIEND_RES_NOTIFY.value)
        pw.writeInt(cid) // dwFriendID
        pw.write(0) // aInShop
        pw.writeInt(channel) // nChannelID

        return pw.createPacket()
    }

    private fun encodeGWFriend(pw: PacketWriter, group: String, fid: Int, name: String, flag: Int, channel: Int) {
        pw.writeInt(fid) // dwFriendID
        pw.writeFixedString(name, 13) // sFriendName[13]
        pw.write(flag) // nFlag
        pw.writeInt(channel) // nChannelID
        pw.writeFixedString(group, 17) // sFriendGroup[17]
    }

    fun sendFriendRequest(name: String, group: String) {
        val friend = owner.getChannel().getCharacter(name)

        if (friend != null) {
            addFriend(friend, group, false)
            addFriend(owner.id, friend.id, group, true)
            updateFriendList()
            val friendFriendList = friend.friendList // lul

            synchronized(pending) {
                if (!friendFriendList.friends.containsKey(owner.id)) {
                    if (friendFriendList.pending.isEmpty()) {
                        friend.write(getSendFriendRequestPacket(group))
                    } else {
                        friendFriendList.pending.add(owner.id)
                    }
                } else {
                    friend.friendList.updateFriendList()
                }
            }
        } else {
            val id = getOfflineId(name)

            if (id == -1) {
                sendFriendMessage(FriendOperation.FRIEND_RES_SET_FRIEND_UNKNOWN_USER)
                return
            }

            addFriend(id, name, group)
            addFriend(owner.id, id, group, true)
            updateFriendList()
        }
    }

    fun updateFriendList() {
        owner.write(updateFriendsPacket)
    }

    private fun getSendFriendRequestPacket(group: String, cid: Int, name: String, level: Int, job: Int, channel: Int): Packet {
        val pw = PacketWriter(8)

        pw.writeHeader(SendOpcode.FRIEND_RESULT)
        pw.write(FriendOperation.FRIEND_RES_INVITE.value)
        pw.writeInt(cid) // dwFriendID
        pw.writeMapleString(name) // v24
        pw.writeInt(level) // nLevel
        pw.writeInt(job) // nJobCode
        encodeGWFriend(pw, group, cid, name, 1, channel)
        pw.write(0) // aInShop?

        return pw.createPacket()
    }

    private fun getSendFriendRequestPacket(group: String): Packet {
        return getSendFriendRequestPacket(group, owner.id, owner.name, owner.level, owner.job.value, owner.getChannel().channelId)
    }

    /**
     * For pending requests
     *
     * @param cid character id of person that sent the request
     * @return invite packet
     */
    private fun getSendFriendRequestPacket(cid: Int): Packet {
        return getCharacter(cid)?.friendList?.getSendFriendRequestPacket("Group Unknown") ?: run {
            val rec = getOfflineCharacter(cid)
            getSendFriendRequestPacket(
                    "Group Unknown", cid,
                    rec.getValue(Tables.CHARACTERS.NAME),
                    rec.getValue(Tables.CHARACTERS.LEVEL),
                    rec.getValue(Tables.CHARACTERS.JOB),
                    -1)
        }
    }

    fun notifyMutualFriends() {
        if (!owner.client.isCc) {
            val channel = if (owner.client.isDisconnecting) -1 else owner.getChannel().channelId

            synchronized(friends) {
                friends.keys.forEach { id ->
                    getCharacter(id)?.let { chr ->
                        chr.friendList.friends[owner.id]?.let {
                            it.channel = channel
                            chr.write(getFriendChannelChangePacket(owner.id, channel))
                            chr.friendList.updateFriendList()
                        }
                    }
                }
            }
        }
    }

    fun sendPendingRequest() {
        synchronized(pending) {
            pending.poll()?.let {
                owner.write(getSendFriendRequestPacket(it))
            }
        }
    }

    fun sendFriendMessage(operation: FriendOperation) {
        val pw = PacketWriter(3)

        pw.writeHeader(SendOpcode.FRIEND_RESULT)
        pw.write(operation.value)

        owner.write(pw.createPacket())
    }
}