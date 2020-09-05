package client.player.friend;

import client.Character;
import net.database.CharacterAPI;
import net.database.FriendAPI;
import net.maple.SendOpcode;
import net.maple.handlers.group.FriendRequestHandler;
import net.server.Server;
import org.jooq.Record;
import util.packet.Packet;
import util.packet.PacketWriter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static database.jooq.Tables.CHARACTERS;

public class FriendList {

    private final Character owner;
    private final Map<Integer, Friend> friends = new LinkedHashMap<>();
    private final Queue<Integer> pending = new LinkedBlockingQueue<>();

    public FriendList(Character owner) {
        this.owner = owner;
    }

    public Map<Integer, Friend> getFriends() {
        return friends;
    }

    public Queue<Integer> getPending() {
        return pending;
    }

    public void addFriend(Character friend, String group, boolean online) {
        addFriend(friend.getId(), friend.getChannel().getChannelId(), friend.getName(), group, online);
    }

    public void addFriend(int id, String name, String group) {
        addFriend(id, -1, name, group, false);
    }

    public void addFriend(int id, int channel, String name, String group, boolean online) {
        friends.put(id, new Friend(id, channel, name, group, online));
    }

    public void removeFriend(int id) {
        friends.remove(id);
    }

    public void sendMessage(Packet packet) {
        friends.keySet().forEach(friendId -> {
            Character friend = Server.Companion.getInstance().getCharacter(friendId);
            if (friend != null && friend.getFriendList().getFriends().get(owner.getId()) != null) {
                friend.write(packet);
            }
        });
    }

    public Packet getUpdateFriendsPacket() {
        PacketWriter pw = new PacketWriter(8);

        pw.writeHeader(SendOpcode.FRIEND_RESULT);
        pw.write(FriendRequestHandler.FriendOperation.FRIEND_RES_LOAD_FRIEND_DONE.getValue());

        pw.write(friends.size());
        for (Friend f : friends.values()) {
            boolean visible = f.isOnline() && Server.Companion.getInstance().getCharacter(f.getCharacterId()).getFriendList().getFriends().containsKey(owner.getId());
            encodeGWFriend(pw, f.getGroup(), f.getCharacterId(), f.getName(), visible ? 0 : 2, f.getChannel());
        }

        for (int i = 0; i < friends.size(); i++) {
            pw.writeInt(0);
        }

        return pw.createPacket();
    }

    public Packet getFriendChannelChangePacket(int cid, int channel) {
        PacketWriter pw = new PacketWriter(12);

        pw.writeHeader(SendOpcode.FRIEND_RESULT);
        pw.write(FriendRequestHandler.FriendOperation.FRIEND_RES_NOTIFY.getValue());

        pw.writeInt(cid); // dwFriendID
        pw.write(0); // aInShop
        pw.writeInt(channel); // nChannelID

        return pw.createPacket();
    }

    private void encodeGWFriend(PacketWriter pw, String group, int fid, String name, int flag, int channel) {
        pw.writeInt(fid); // dwFriendID
        pw.writeFixedString(name, 13); // sFriendName[13]
        pw.write(flag); // nFlag
        pw.writeInt(channel); // nChannelID
        pw.writeFixedString(group, 17); // sFriendGroup[17]
    }

    public void sendFriendRequest(String name, String group) {
        Character friend = owner.getChannel().getCharacter(name);
        if (friend != null) {
            addFriend(friend, group, false);
            FriendAPI.INSTANCE.addFriend(owner.getId(), friend.getId(), group, true);
            updateFriendList();
            FriendList friendFriendList = friend.getFriendList(); // lul
            if (!friendFriendList.getFriends().containsKey(owner.getId())) {
                if (friendFriendList.getPending().isEmpty()) {
                    friend.write(getSendFriendRequestPacket(group));
                } else {
                    friendFriendList.getPending().add(owner.getId());
                }
            } else {
                friend.getFriendList().updateFriendList();
            }
        } else {
            int id = CharacterAPI.INSTANCE.getOfflineId(name);
            if (id == -1) {
                sendFriendMessage(FriendRequestHandler.FriendOperation.FRIEND_RES_SET_FRIEND_UNKNOWN_USER);
                return;
            }
            addFriend(id, name, group);
            FriendAPI.INSTANCE.addFriend(owner.getId(), id, group, true);
            updateFriendList();
        }
    }

    public void updateFriendList() {
        owner.write(getUpdateFriendsPacket());
    }

    private Packet getSendFriendRequestPacket(String group, int cid, String name, int level, int job, int channel) {
        PacketWriter pw = new PacketWriter(8);

        pw.writeHeader(SendOpcode.FRIEND_RESULT);
        pw.write(FriendRequestHandler.FriendOperation.FRIEND_RES_INVITE.getValue());

        pw.writeInt(cid); // dwFriendID
        pw.writeMapleString(name); // v24
        pw.writeInt(level); // nLevel
        pw.writeInt(job); // nJobCode

        encodeGWFriend(pw, group, cid, name, 1, channel);
        pw.write(0); // aInShop?

        return pw.createPacket();
    }

    public Packet getSendFriendRequestPacket(String group) {
        return getSendFriendRequestPacket(group, owner.getId(), owner.getName(), owner.getLevel(), owner.getJob().getValue(), owner.getChannel().getChannelId());
    }

    /**
     * For pending requests
     *
     * @param cid character id of person that sent the request
     * @return invite packet
     */
    public Packet getSendFriendRequestPacket(int cid) {
        Character friend = Server.Companion.getInstance().getCharacter(cid);
        if (friend != null) {
            return friend.getFriendList().getSendFriendRequestPacket("Group Unknown");
        } else {
            Record rec = CharacterAPI.INSTANCE.getOfflineCharacter(cid);
            return getSendFriendRequestPacket(
                    "Group Unknown", cid,
                    rec.getValue(CHARACTERS.NAME),
                    rec.getValue(CHARACTERS.LEVEL),
                    rec.getValue(CHARACTERS.JOB),
                    -1
            );
        }
    }

    public void notifyMutualFriends() {
        if (!owner.getClient().isCc()) {
            final int channel = owner.getClient().isDisconnecting() ? -1 : owner.getChannel().getChannelId();
            friends.keySet().forEach(f -> {
                Character friend = Server.Companion.getInstance().getCharacter(f);
                if (friend != null) {
                    if (friend.getFriendList().friends.containsKey(owner.getId())) {
                        friend.getFriendList().friends.get(owner.getId()).setChannel(channel);
                        friend.write(getFriendChannelChangePacket(owner.getId(), channel));
                        friend.getFriendList().updateFriendList();
                    }
                }
            });
        }
    }

    public void sendPendingRequest() {
        Integer nextFriend = pending.poll();
        if (nextFriend != null) {
            owner.write(getSendFriendRequestPacket(nextFriend));
        }
    }

    public void sendFriendMessage(FriendRequestHandler.FriendOperation operation) {
        PacketWriter pw = new PacketWriter(3);

        pw.writeHeader(SendOpcode.FRIEND_RESULT);
        pw.write(operation.getValue());

        owner.write(pw.createPacket());
    }
}
