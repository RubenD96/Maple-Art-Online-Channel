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
                friend.write(getSendFriendRequestPacket(c.getCharacter(), friend.getId()));
            } else {
                // todo print to game
                System.out.println(name + " not found!");
            }
        }
    }

    private Packet getSendFriendRequestPacket(Character from, int cid) {
        PacketWriter pw = new PacketWriter(8);

        pw.writeHeader(SendOpcode.FRIEND_RESULT);
        pw.write(FriendRequestOperationType.SEND);

        pw.writeInt(from.getId());
        pw.writeMapleString(from.getName());
        pw.writeInt(from.getId());
        pw.writeString(from.getName());
        pw.fill(0x00, 11 - from.getName().length());

        pw.write(0x09);
        pw.write(0xf0);
        pw.write(0x01);
        pw.writeInt(0x0f);
        pw.writeNullTerminatedString("Group Unknown");
        pw.writeInt(cid);

        return pw.createPacket();
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
