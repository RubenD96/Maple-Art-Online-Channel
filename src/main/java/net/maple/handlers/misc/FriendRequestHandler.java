package net.maple.handlers.misc;

import client.Character;
import client.Client;
import client.player.friend.Friend;
import client.player.friend.FriendList;
import net.database.FriendAPI;
import net.maple.handlers.PacketHandler;
import util.HexTool;
import util.packet.PacketReader;

public class FriendRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        System.out.println(HexTool.toHex(reader.getData()));
        byte operation = reader.readByte();
        FriendList friendList = c.getCharacter().getFriendList();

        if (operation == FriendRequestOperationType.SET) {
            friendList.sendFriendRequest(reader);
        } else if (operation == FriendRequestOperationType.ACCEPT) {
            int cid = reader.read(); // todo, check if this person actually sent a friendrequest?
            Character toAdd = c.getWorldChannel().getCharacter(cid);
            if (toAdd != null) {
                friendList.addFriend(toAdd, "Group Unknown", true);
                FriendAPI.addFriend(c.getCharacter().getId(), toAdd.getId(), "Group Unknown");
                toAdd.getFriendList().updateFriendList();
            } else {
                // todo get from DB
            }
            friendList.updateFriendList();
        } else if (operation == FriendRequestOperationType.DELETE) {
            int cid = reader.read();
            if (friendList.getFriends().containsKey(cid)) {
                friendList.removeFriend(cid);
                FriendAPI.removeFriend(c.getCharacter().getId(), cid);
                Character toRemove = c.getWorldChannel().getCharacter(cid);
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

        public static final byte SET = 1;
        public static final byte ACCEPT = 2;
        public static final byte DELETE = 3;
        public static final byte UPDATE = 7;
        public static final byte SEND = 9;

        private FriendRequestOperationType() {
        }
    }
}
