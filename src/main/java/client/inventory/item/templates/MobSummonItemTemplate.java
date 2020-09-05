package client.inventory.item.templates;

import util.packet.PacketReader;

import java.util.ArrayList;
import java.util.List;

public class MobSummonItemTemplate extends ItemBundleTemplate {

    private final List<MobSummonItemEntry> mobs = new ArrayList<>();

    public List<MobSummonItemEntry> getMobs() {
        return mobs;
    }

    public MobSummonItemTemplate(int id, PacketReader r) {
        super(id, r);
        int size = r.readShort();
        for (int i = 0; i < size; i++) {
            mobs.add(new MobSummonItemEntry(r));
        }
    }

    public static class MobSummonItemEntry {

        private final int templateId, prob;

        public int getTemplateId() {
            return templateId;
        }

        public int getProb() {
            return prob;
        }

        public MobSummonItemEntry(PacketReader r) {
            this.templateId = r.readInteger();
            this.prob = r.readInteger();
        }
    }
}
