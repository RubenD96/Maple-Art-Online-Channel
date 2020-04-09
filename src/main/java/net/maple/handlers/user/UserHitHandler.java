package net.maple.handlers.user;

import client.Character;
import client.Client;
import net.maple.handlers.PacketHandler;
import net.maple.packets.CharacterPackets;
import util.packet.PacketReader;

public class UserHitHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();

        int timestamp = reader.readInteger();
        byte type = reader.readByte();
        byte element = reader.readByte();
        int dmg = reader.readInteger();
        /*System.out.println(timestamp);
        System.out.println(type);
        System.out.println(element);
        System.out.println(dmg);*/

        switch (type) {
            case DamageType.MOB:
                int mobId = reader.readInteger();
                int objId = reader.readInteger();
                CharacterPackets.showDamage(chr, type, dmg, mobId, objId);
                break;
            case DamageType.WORLD:
                CharacterPackets.showDamage(chr, type, dmg, 0, 0);
                break;
            default:
                System.err.println("[UserHitHandler] Unknown damage type (" + type + ")");
                break;
        }
    }

    private static final class DamageType {

        public static final byte MOB = -1;
        public static final byte WORLD = -3;

        private DamageType() {
        }
    }
}