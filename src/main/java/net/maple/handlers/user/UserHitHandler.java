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
        byte guard = reader.readByte();
        int dmg = reader.readInteger();

        switch (type) {
            case DamageType.MOB_PYSHICAL:
            case DamageType.MOB_MAGIC:
                int mobId = reader.readInteger();
                int objId = reader.readInteger();
                byte left = reader.readByte();
                byte top = reader.readByte();
                byte relativeDir = reader.readByte();
                byte damageMissed = reader.readByte();
                byte v284x = reader.readByte();
                CharacterPackets.showDamage(chr, type, dmg, mobId, left);
                chr.modifyHealth(-dmg);
                break;
            case DamageType.OBSTACLE:
                CharacterPackets.showDamage(chr, type, dmg, 0, (byte) 0);
                chr.modifyHealth(-dmg);
                break;
            default:
                System.err.println("[UserHitHandler] Unknown damage type (" + type + ")");
                break;
        }
    }

    private static final class DamageType {

        // these nexon enums seem to be wrong
        //public static final byte MOB_PYSHICAL = 0x0;
        //public static final byte MOB_MAGIC = 0xFFFFFFFF;
        public static final byte MOB_MAGIC = 0x0;
        public static final byte MOB_PYSHICAL = 0xFFFFFFFF;
        public static final byte COUNTER = 0xFFFFFFFE;
        public static final byte OBSTACLE = 0xFFFFFFFD;
        public static final byte STAT = 0xFFFFFFFC;

        private DamageType() {
        }
    }
}