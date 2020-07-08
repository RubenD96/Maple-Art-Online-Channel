package client.player.friend;

import client.Character;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.database.FriendAPI;
import net.maple.SendOpcode;
import net.maple.handlers.misc.FriendRequestHandler;
import net.server.Server;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class FriendList {

    private @NonNull final Character owner;
    private @Getter Map<Integer, Friend> friends = new LinkedHashMap<>();

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

    public Packet getUpdateFriendsPacket() {
        PacketWriter pw = new PacketWriter(8);

        pw.writeHeader(SendOpcode.FRIEND_RESULT);
        pw.write(FriendRequestHandler.FriendRequestOperationType.UPDATE);

        pw.write(friends.size());
        for (Friend f : friends.values()) {
            boolean visible = f.isOnline() && Server.getInstance().getCharacter(f.getCharacterId()).getFriendList().getFriends().containsKey(owner.getId());
            encodeGWFriend(pw, f.getGroup(), f.getCharacterId(), f.getName(), visible ? 0 : 2, f.getChannel());
        }

        for (int i = 0; i < friends.size(); i++) {
            pw.writeInt(0);
        }

        return pw.createPacket();
    }

    private void encodeGWFriend(PacketWriter pw, String group, int fid, String name, int flag, int channel) {
        pw.writeInt(fid); // dwFriendID
        pw.writeString(name); // sFriendName
        pw.fill(0x00, 13 - name.length()); // [13]
        pw.write(flag); // nFlag
        pw.writeInt(channel); // nChannelID
        pw.writeString(group); // sFriendGroup
        pw.fill(0x00, 17 - group.length()); // [17]
    }

    private void encodeGWFriend(PacketWriter pw, String group, int flag) {
        encodeGWFriend(pw, group, owner.getId(), owner.getName(), flag, owner.getChannel().getChannelId());
    }

    public void sendFriendRequest(PacketReader reader) {
        String name = reader.readMapleString();
        String group = reader.readMapleString();
        if (name.length() < 4 || name.length() > 12 || group.length() > 16) {
            owner.getClient().close(this, "Invalid name/group on SET friend request (" + name + "/" + group + ")");
            return;
        }
        if (name.equals(owner.getName())) {
            owner.getClient().close(this, "Adding yourself as friend");
            return;
        }

        Character friend = owner.getChannel().getCharacter(name);
        if (friend != null) {
            addFriend(friend, group, false);
            FriendAPI.addFriend(owner.getId(), friend.getId(), group);
            updateFriendList();
            if (!friend.getFriendList().getFriends().containsKey(owner.getId())) {
                friend.write(getSendFriendRequestPacket(group));
            } else {
                friend.getFriendList().updateFriendList();
            }
        } else {
            // todo print to game
            System.out.println(name + " not found!");
        }
    }

    public void updateFriendList() {
        owner.write(getUpdateFriendsPacket());
    }

    public Packet getSendFriendRequestPacket(String group) {
        PacketWriter pw = new PacketWriter(8);

        pw.writeHeader(SendOpcode.FRIEND_RESULT);
        pw.write(FriendRequestHandler.FriendRequestOperationType.SEND);

        pw.writeInt(owner.getId()); // dwFriendID
        pw.writeMapleString(owner.getName()); // v24
        pw.writeInt(owner.getLevel()); // nLevel
        pw.writeInt(owner.getJob()); // nJobCode

        encodeGWFriend(pw, group, 1);
        pw.write(0); // aInShop?

        return pw.createPacket();
    }

    public void notifyMutualFriends() {
        friends.keySet().forEach(f -> {
            Character friend = Server.getInstance().getCharacter(f);
            if (friend != null) {
                System.out.println(owner.getName() + " - " + friend.getName());
                if (friend.getFriendList().friends.containsKey(owner.getId())) {
                    friend.getFriendList().updateFriendList();
                }
            }
        });
    }
}
