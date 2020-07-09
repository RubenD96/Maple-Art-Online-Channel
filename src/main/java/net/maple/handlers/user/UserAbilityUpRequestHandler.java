package net.maple.handlers.user;

import client.Character;
import client.Client;
import client.player.StatType;
import net.maple.handlers.PacketHandler;
import util.packet.PacketReader;

import java.util.ArrayList;
import java.util.List;

public class UserAbilityUpRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        reader.readInteger(); // ?

        int type = reader.readInteger();
        if (chr.getAp() > 0) {
            chr.decAP();
            List<StatType> statTypes = new ArrayList<>();
            statTypes.add(StatType.AP);

            if (type == StatType.STR.getStat()) {
                chr.incStrength();
                statTypes.add(StatType.STR);
            } else if (type == StatType.DEX.getStat()) {
                chr.incDexterity();
                statTypes.add(StatType.DEX);
            } else if (type == StatType.INT.getStat()) {
                chr.incIntelligence();
                statTypes.add(StatType.INT);
            } else if (type == StatType.LUK.getStat()) {
                chr.incLuck();
                statTypes.add(StatType.LUK);
            }

            chr.updateStats(statTypes, true);
        }
    }
}
