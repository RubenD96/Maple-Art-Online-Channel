package client.interaction.storage;

import client.inventory.ItemInventory;
import lombok.Getter;
import lombok.Setter;

public class ItemStorage extends ItemInventory {

    private @Getter @Setter int meso;

    public ItemStorage(short slotMax, int meso) {
        super(slotMax);
        this.meso = meso;
    }
}
