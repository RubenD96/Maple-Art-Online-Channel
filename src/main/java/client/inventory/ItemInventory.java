package client.inventory;

import client.inventory.slots.ItemSlot;

import java.util.HashMap;
import java.util.Map;

public class ItemInventory {

    private short slotMax;
    private final Map<Short, ItemSlot> items;

    public short getSlotMax() {
        return slotMax;
    }

    public void setSlotMax(short slotMax) {
        this.slotMax = slotMax;
    }

    public Map<Short, ItemSlot> getItems() {
        return items;
    }

    public ItemInventory(short slotMax) {
        this.slotMax = slotMax;
        items = new HashMap<>();
    }
}
