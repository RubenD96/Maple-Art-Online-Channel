package net.maple.handlers.misc;

import client.Character;
import client.Client;
import lombok.RequiredArgsConstructor;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class GroupMessageHandler extends PacketHandler {

    @RequiredArgsConstructor
    private static final class ChatGroupType {
        public static final byte FRIEND = 0x00;
        public static final byte PARTY = 0x01;
        public static final byte GUILD = 0x02;
        public static final byte ALLIANCE = 0x03;
        public static final byte COUPLE = 0x04;
        public static final byte TO_COUPLE = 0x05;
        public static final byte EXPEDITION = 0x06;
    }

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        reader.readInteger(); // timestamp
        byte type = reader.readByte(); // chat type
        int size = reader.readByte(); // count members
        for (int i = 0; i < size; i++) {
            reader.readInteger(); // cid
        }
        String message = reader.readMapleString(); // the message

        switch (type) {
            case ChatGroupType.FRIEND:
                chr.getFriendList().sendMessage(multiChat(ChatGroupType.FRIEND, chr.getName(), message));
                break;
            case ChatGroupType.PARTY:
                if (chr.getParty() != null) {
                    chr.getParty().sendMessage(multiChat(ChatGroupType.PARTY, chr.getName(), message), chr.getId());
                }
            case ChatGroupType.GUILD:
                if (chr.getGuild() != null) {
                    chr.getGuild().broadcast(multiChat(ChatGroupType.GUILD, chr.getName(), message), chr);
                }
                break;
            default:
                System.out.println("Unknown GroupMessageHandler type (" + type + ") from " + chr.getName());
                break;

        }
    }

    public static Packet multiChat(byte type, String name, String message) {
        PacketWriter pw = new PacketWriter(8);

        pw.writeHeader(SendOpcode.GROUP_MESSAGE);
        pw.write(type);
        pw.writeMapleString(name);
        pw.writeMapleString(message);

        return pw.createPacket();
    }
}
