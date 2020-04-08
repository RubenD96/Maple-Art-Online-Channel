package net.maple.handlers.user;

import client.Character;
import client.Client;
import client.player.StatType;
import net.maple.handlers.PacketHandler;
import util.packet.PacketReader;

public class UserChangeStatRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();

        reader.readInteger(); // timestamp
        int flag = reader.readInteger();
        int hp = 0;
        int mp = 0;

        if ((flag & StatType.HP.getStat()) == StatType.HP.getStat()) {
            hp = reader.readShort();
        }

        if ((flag & StatType.MP.getStat()) == StatType.MP.getStat()) {
            mp = reader.readShort();
        }

        if (hp > 0 || mp > 0) {
            chr.modifyHPMP(hp, mp);
        }
    }
}
