package net.maple.handlers.misc;

import client.Character;
import client.Client;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class FriendRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        byte operation = reader.readByte();

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

            Character friend = c.getWorldChannel().getCharacterByName(name);
            if (friend != null) {
                friend.write(getSendFriendRequestPacket(c.getCharacter(), group));
            } else {
                // todo print to game
                System.out.println(name + " not found!");
            }
        }
    }

    private Packet getSendFriendRequestPacket(Character from, String group) {
        PacketWriter pw = new PacketWriter(8);

        pw.writeHeader(SendOpcode.FRIEND_RESULT);
        pw.write(FriendRequestOperationType.SEND);

        pw.writeInt(from.getId()); // dwFriendID
        pw.writeMapleString(from.getName()); // v24
        pw.writeInt(from.getLevel()); // nLevel
        pw.writeInt(from.getJob()); // nJobCode

        encodeGWFriend(pw, from, group);
        pw.write(0); // aInShop?

        return pw.createPacket();
    }

    private void encodeGWFriend(PacketWriter pw, Character from, String group) {
        pw.writeInt(from.getId()); // dwFriendID
        pw.writeString(from.getName()); // sFriendName
        pw.fill(0x00, 13 - from.getName().length()); // [13]
        pw.write(0); // nFlag
        pw.writeInt(from.getChannel().getChannelId()); // nChannelID
        pw.writeString(group);
        pw.fill(0x00, 17 - group.length());
    }

    private static final class FriendRequestOperationType {

        public static final byte SET = 1;
        public static final byte ACCEPT = 2;
        public static final byte DELETE = 3;
        public static final byte SEND = 9;

        private FriendRequestOperationType() {
        }
    }
}
