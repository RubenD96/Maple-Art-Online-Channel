package net.maple.handlers.misc;

import client.Character;
import client.Client;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class GroupMessageHandler extends PacketHandler {

    @RequiredArgsConstructor
    private enum ChatGroupType {
        FRIEND(0x00),
        PARTY(0x01),
        GUILD(0x02),
        ALLIANCE(0x03),
        COUPLE(0x04),
        TO_COUPLE(0x05),
        EXPEDITION(0x06);

        @Getter @NonNull private final int value;
    }

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        reader.readInteger(); // timestamp
        int type = reader.readByte(); // chat type
        int size = reader.readByte(); // count members
        for (int i = 0; i < size; i++) {
            reader.readInteger(); // cid
        }
        String message = reader.readMapleString(); // the message

        if (type == ChatGroupType.FRIEND.value) {
            chr.getFriendList().sendMessage(multiChat(ChatGroupType.FRIEND, chr.getName(), message));
        } else if (type == ChatGroupType.PARTY.value) {
            if (chr.getParty() != null) {
                chr.getParty().sendMessage(multiChat(ChatGroupType.PARTY, chr.getName(), message), chr.getId());
            }
        } else {
            System.out.println("Unknown GroupMessageHandler type (" + type + ") from " + chr.getName());
        }
    }

    public static Packet multiChat(ChatGroupType type, String name, String message) {
        PacketWriter pw = new PacketWriter(8);

        pw.writeHeader(SendOpcode.GROUP_MESSAGE);
        pw.write(type.value);
        pw.writeMapleString(name);
        pw.writeMapleString(message);

        return pw.createPacket();
    }
}
