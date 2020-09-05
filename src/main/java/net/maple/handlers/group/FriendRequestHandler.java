package net.maple.handlers.group;

import client.Character;
import client.Client;
import client.player.friend.Friend;
import client.player.friend.FriendList;
import net.database.CharacterAPI;
import net.database.FriendAPI;
import net.maple.handlers.PacketHandler;
import net.server.Server;
import util.packet.PacketReader;

public class FriendRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        byte operation = reader.readByte();
        FriendList friendList = c.getCharacter().getFriendList();

        if (operation == FriendOperation.FRIEND_REQ_SET_FRIEND.getValue()) {
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

            Friend friend = friendList.getFriends().get(CharacterAPI.INSTANCE.getOfflineId(name));
            if (friend == null) {
                friendList.sendFriendRequest(name, group);
            } else {
                friend.setGroup(group);
                FriendAPI.INSTANCE.updateGroup(c.getCharacter().getId(), friend.getCharacterId(), group);
                friendList.updateFriendList();
            }
        } else if (operation == FriendOperation.FRIEND_REQ_ACCEPT_FRIEND.getValue()) {
            int cid = reader.read();
            Character toAdd = Server.Companion.getInstance().getCharacter(cid);
            if (toAdd != null) {
                friendList.addFriend(toAdd, "Group Unknown", true);
                FriendAPI.INSTANCE.addFriend(c.getCharacter().getId(), toAdd.getId(), "Group Unknown", false);
                FriendAPI.INSTANCE.removePendingStatus(toAdd.getId(), c.getCharacter().getId());
                Friend f = toAdd.getFriendList().getFriends().get(c.getCharacter().getId());
                if (f != null) {
                    f.setChannel(c.getCharacter().getChannel().getChannelId());
                }
                toAdd.getFriendList().updateFriendList();
            } else { // player is offline already
                String name = CharacterAPI.INSTANCE.getOfflineName(cid);
                if (!name.equals("")) {
                    friendList.addFriend(cid, CharacterAPI.INSTANCE.getOfflineName(cid), "Group Unknown");
                    FriendAPI.INSTANCE.addFriend(c.getCharacter().getId(), cid, "Group Unknown", false);
                    FriendAPI.INSTANCE.removePendingStatus(cid, c.getCharacter().getId());
                } else {
                    friendList.sendFriendMessage(FriendRequestHandler.FriendOperation.FRIEND_RES_SET_FRIEND_UNKNOWN_USER);
                }
            }
            friendList.updateFriendList();
            friendList.sendPendingRequest();
        } else if (operation == FriendOperation.FRIEND_REQ_DELETE_FRIEND.getValue()) {
            int cid = reader.read();
            if (friendList.getFriends().containsKey(cid)) {
                friendList.removeFriend(cid);
                FriendAPI.INSTANCE.removeFriend(c.getCharacter().getId(), cid);
                Character toRemove = Server.Companion.getInstance().getCharacter(cid);
                if (toRemove != null) {
                    toRemove.getFriendList().updateFriendList();
                }
                friendList.updateFriendList();
            } else {
                c.close(this, "Deleting unknown friend");
            }
        }
    }

    public enum FriendOperation {

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

        private final int value;

        FriendOperation(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
