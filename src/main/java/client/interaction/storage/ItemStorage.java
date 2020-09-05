package client.interaction.storage;

import client.inventory.ItemInventory;

public class ItemStorage extends ItemInventory {

    private int meso;

    public int getMeso() {
        return meso;
    }

    public void setMeso(int meso) {
        this.meso = meso;
    }

    public ItemStorage(short slotMax, int meso) {
        super(slotMax);
        this.meso = meso;
    }
}
