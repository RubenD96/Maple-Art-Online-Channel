package net.maple.handlers.misc;

import client.Character;
import client.Client;
import client.player.key.KeyBinding;
import net.maple.handlers.PacketHandler;
import util.packet.PacketReader;

public class FuncKeyMappedModifiedHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();

        reader.readInteger(); // bunch of zeroes

        int changed = reader.readInteger();
        for (int i = 0; i < changed; i++) {
            int key = reader.readInteger();
            byte type = reader.readByte();
            int action = reader.readInteger();
            chr.getKeyBindings().put(key, new KeyBinding(
                    type, // type
                    action // action
            ));
        }
    }
}
