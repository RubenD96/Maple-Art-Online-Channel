package client.effects.user;

import client.effects.AbstractEffect;
import client.effects.EffectType;
import org.graalvm.collections.Pair;
import util.packet.PacketWriter;

import java.util.ArrayList;
import java.util.List;

public class QuestEffect extends AbstractEffect {

    private List<Pair<Integer, Integer>> entries = new ArrayList<>();

    public QuestEffect(int id, int quantity) {
        entries.add(Pair.create(id, quantity));
    }

    public QuestEffect(List<Pair<Integer, Integer>> entries) {
        this.entries = entries;
    }

    @Override
    public EffectType getType() {
        return EffectType.QUEST;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.write(entries.size());
        entries.forEach(entry -> {
            pw.writeInt(entry.getLeft());
            pw.writeInt(entry.getRight());
        });
    }
}
