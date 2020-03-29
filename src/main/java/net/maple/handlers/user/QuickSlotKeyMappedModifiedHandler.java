package net.maple.handlers.user;

import net.maple.handlers.PacketHandler;
import player.Character;
import player.Client;
import util.packet.PacketReader;

public class QuickSlotKeyMappedModifiedHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        for (int i = 0; i < 8; i++) {
            chr.getQuickSlotKeys()[i] = reader.readInteger();
        }
    }
}
