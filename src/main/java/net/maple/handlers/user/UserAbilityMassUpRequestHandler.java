package net.maple.handlers.user;

import client.Character;
import client.Client;
import client.player.StatType;
import net.maple.handlers.PacketHandler;
import util.packet.PacketReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAbilityMassUpRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        reader.readInteger(); // ?

        int count = reader.readInteger();
        Map<Integer, Integer> inc = new HashMap<>();
        int total = 0;

        for (int i = 0; i < count; i++) {
            int type = reader.readInteger();
            int amount = reader.readInteger();
            inc.put(type, amount);
            total += amount;
        }

        if (chr.getAp() < total) {
            c.close(this, "More stats than AP available");
            return;
        }

        List<StatType> statTypes = new ArrayList<>();
        statTypes.add(StatType.AP);
        chr.setAp(chr.getAp() - total);

        inc.forEach((type, amount) -> {
            if (type == StatType.STR.getStat()) {
                chr.setStrength(chr.getStrength() + amount);
                statTypes.add(StatType.STR);
            } else if (type == StatType.DEX.getStat()) {
                chr.setDexterity(chr.getDexterity() + amount);
                statTypes.add(StatType.DEX);
            } else if (type == StatType.INT.getStat()) {
                chr.setIntelligence(chr.getIntelligence() + amount);
                statTypes.add(StatType.INT);
            } else if (type == StatType.LUK.getStat()) {
                chr.setLuck(chr.getLuck() + amount);
                statTypes.add(StatType.LUK);
            }
        });

        chr.updateStats(statTypes, true);
    }
}
