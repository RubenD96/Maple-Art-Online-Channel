package client.inventory;

import client.inventory.item.templates.ItemTemplate;
import client.inventory.slots.ItemSlot;
import client.inventory.slots.ItemSlotBundle;
import util.packet.PacketWriter;

public interface ModifyInventoryContextInterface {

    void add(ItemSlot item);

    void add(ItemTemplate item, short quantity);

    void set(short slot, ItemSlot item);

    void set(short slot, ItemTemplate item, short quantity);

    void remove(short slot);

    void remove(short slot, short quantity);

    void remove(ItemSlot item);

    void remove(ItemSlot item, short quantity);

    void remove(int id, short quantity);

    void move(short from, short to);

    ItemSlotBundle take(short slot, short quantity);

    ItemSlotBundle take(ItemSlotBundle bundle, short quantity);

    ItemSlotBundle take(int id, short quantity);

    void update(short slot);

    void update(ItemSlot item);

    void encode(PacketWriter pw);
}
