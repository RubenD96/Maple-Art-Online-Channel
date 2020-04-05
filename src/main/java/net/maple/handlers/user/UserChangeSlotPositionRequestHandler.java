package net.maple.handlers.user;

import client.Character;
import client.Client;
import client.inventory.ItemInventoryType;
import net.maple.handlers.PacketHandler;
import net.maple.packets.CharacterPackets;
import util.packet.PacketReader;

public class UserChangeSlotPositionRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();

        reader.readInteger();
        ItemInventoryType type = ItemInventoryType.values()[reader.readByte() - 1];
        short from = reader.readShort();
        short to = reader.readShort();
        short number = reader.readShort();

        if (to == 0) { // drop
            System.out.println("Drop from " + type.name());
        } else {
            CharacterPackets.modifyInventory(chr,
                    i -> i.getInventoryContext(type).move(from, to),
                    true);
        }
    }
}
