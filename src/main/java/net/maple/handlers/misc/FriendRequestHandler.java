package net.maple.handlers.misc;

import client.Character;
import client.Client;
import client.player.friend.Friend;
import client.player.friend.FriendList;
import net.database.CharacterAPI;
import net.database.FriendAPI;
import net.maple.handlers.PacketHandler;
import net.server.Server;
import util.HexTool;
import util.packet.PacketReader;

public class FriendRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        byte operation = reader.readByte();
        FriendList friendList = c.getCharacter().getFriendList();

        if (operation == FriendRequestOperationType.SET) {
            String name = reader.readMapleString();
            String group = reader.readMapleString();
            if (name.length() < 4 || name.length() > 12 || group.length() > 16) {
                c.close(this, "Invalid name/group on SET friend request (" + name + "/" + group + ")");
                return;
            }
            if (name.equals(c.getCharacter().getName())) {
                c.close(this, "Adding yourself as friend");
                return;
            }

            Friend friend = friendList.getFriends().get(CharacterAPI.getOfflineId(name));
            if (friend == null) {
                friendList.sendFriendRequest(name, group);
            } else {
                System.out.println(HexTool.toHex(reader.getData()));
                friend.setGroup(group);
                FriendAPI.updateGroup(c.getCharacter().getId(), friend.getCharacterId(), group);
                friendList.updateFriendList();
            }
        } else if (operation == FriendRequestOperationType.ACCEPT) {
            int cid = reader.read();
            Character toAdd = Server.getInstance().getCharacter(cid);
            if (toAdd != null) {
                friendList.addFriend(toAdd, "Group Unknown", true);
                FriendAPI.addFriend(c.getCharacter().getId(), toAdd.getId(), "Group Unknown", false);
                FriendAPI.removePendingStatus(toAdd.getId(), c.getCharacter().getId());
                Friend f = toAdd.getFriendList().getFriends().get(c.getCharacter().getId());
                if (f != null) {
                    f.setChannel(c.getCharacter().getChannel().getChannelId());
                }
                toAdd.getFriendList().updateFriendList();
            } else { // player is offline already
                friendList.addFriend(cid, CharacterAPI.getOfflineName(cid), "Group Unknown");
                FriendAPI.addFriend(c.getCharacter().getId(), cid, "Group Unknown", false);
                FriendAPI.removePendingStatus(cid, c.getCharacter().getId());
            }
            friendList.updateFriendList();
            friendList.sendPendingRequest();
        } else if (operation == FriendRequestOperationType.DELETE) {
            int cid = reader.read();
            if (friendList.getFriends().containsKey(cid)) {
                friendList.removeFriend(cid);
                FriendAPI.removeFriend(c.getCharacter().getId(), cid);
                Character toRemove = Server.getInstance().getCharacter(cid);
                if (toRemove != null) {
                    toRemove.getFriendList().updateFriendList();
                }
                friendList.updateFriendList();
            } else {
                c.close(this, "Removing non-existent friend");
            }
        }
    }

    public static final class FriendRequestOperationType {

        public static final byte SET = 0x01;
        public static final byte ACCEPT = 0x02;
        public static final byte DELETE = 0x03;
        public static final byte UPDATE = 0x07;
        public static final byte SEND = 0x09;
        public static final byte CHANNEL_CHANGE = 0x14;

        private FriendRequestOperationType() {
        }
    }
}
