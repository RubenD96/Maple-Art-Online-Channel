package client.inventory.item.templates;

import lombok.Getter;
import util.packet.PacketReader;

import java.util.ArrayList;
import java.util.List;

public class MobSummonItemTemplate extends ItemBundleTemplate {

    @Getter List<MobSummonItemEntry> mobs = new ArrayList<>();

    public MobSummonItemTemplate(int id, PacketReader r) {
        super(id, r);
        int size = r.readShort();
        for (int i = 0; i < size; i++) {
            mobs.add(new MobSummonItemEntry(r));
        }
    }

    @Getter
    public class MobSummonItemEntry {

        private int templateId, prob;

        public MobSummonItemEntry(PacketReader r) {
            this.templateId = r.readInteger();
            this.prob = r.readInteger();
        }
    }
}
