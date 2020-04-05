package client.inventory;

import client.inventory.item.templates.ItemTemplate;
import client.inventory.operations.AbstractModifyInventoryOperation;
import client.inventory.slots.ItemSlot;
import client.inventory.slots.ItemSlotBundle;
import util.packet.PacketWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifyInventoriesContext {

    private final Map<ItemInventoryType, ModifyInventoryContext> inventories = new HashMap<>();

    public ModifyInventoriesContext(Map<ItemInventoryType, ItemInventory> inventories) {
        for (Map.Entry<ItemInventoryType, ItemInventory> kv : inventories.entrySet()) {
            this.inventories.put(kv.getKey(), new ModifyInventoryContext(kv.getKey(), kv.getValue()));
        }
    }

    public List<AbstractModifyInventoryOperation> getOperations() {
        List<AbstractModifyInventoryOperation> operations = new ArrayList<>();
        inventories.values().stream().map(ModifyInventoryContext::getOperations).forEach(operations::addAll);

        return operations;
    }

    public ModifyInventoryContextInterface getInventoryContext(ItemInventoryType type) {
        return inventories.get(type);
    }

    public ModifyInventoryContextInterface getInventoryByItemId(int id) {
        return getInventoryContext(ItemInventoryType.values()[(id / 1000000) - 1]);
    }

    public void add(ItemSlot item) {
        getInventoryByItemId(item.getTemplateId()).add(item);
    }

    public void add(ItemTemplate item, short quantity) {
        getInventoryByItemId(item.getId()).add(item, quantity);
    }

    public void set(short slot, ItemSlot item) {
        getInventoryByItemId(item.getTemplateId()).set(slot, item);
    }

    public void set(short slot, ItemTemplate item, short quantity) {
        getInventoryByItemId(item.getId()).set(slot, item, quantity);
    }

    public void remove(ItemSlot item) {
        getInventoryByItemId(item.getTemplateId()).remove(item);
    }

    public void remove(ItemSlot item, short quantity) {
        getInventoryByItemId(item.getTemplateId()).remove(item, quantity);
    }

    public void remove(int id, short quantity) {
        getInventoryByItemId(id).remove(id, quantity);
    }

    public ItemSlotBundle take(ItemSlotBundle bundle, short quantity) {
        return getInventoryByItemId(bundle.getTemplateId()).take(bundle, quantity);
    }

    public ItemSlotBundle take(int id, short quantity) {
        return getInventoryByItemId(id).take(id, quantity);
    }

    public void update(ItemSlot item) {
        getInventoryByItemId(item.getTemplateId()).update(item);
    }

    public void encode(PacketWriter pw) {
        List<AbstractModifyInventoryOperation> operations = getOperations();
        pw.writeByte((byte) operations.size());
        operations.forEach(op -> op.encode(pw));
    }
}
