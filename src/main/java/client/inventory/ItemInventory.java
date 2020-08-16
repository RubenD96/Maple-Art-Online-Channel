package client.inventory;

import client.inventory.slots.ItemSlot;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class ItemInventory {

    @Getter @Setter private short slotMax;
    @Getter private final Map<Short, ItemSlot> items;

    public ItemInventory(short slotMax) {
        this.slotMax = slotMax;
        items = new HashMap<>();
    }
}
