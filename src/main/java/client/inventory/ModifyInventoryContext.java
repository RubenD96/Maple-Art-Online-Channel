package client.inventory;

import client.inventory.item.templates.ItemTemplate;
import client.inventory.operations.*;
import client.inventory.slots.ItemSlot;
import client.inventory.slots.ItemSlotBundle;
import constants.ItemConstants;
import lombok.Getter;
import managers.ItemManager;
import util.packet.PacketWriter;

import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ModifyInventoryContext implements ModifyInventoryContextInterface {

    private final ItemInventoryType type;
    private final ItemInventory inventory;

    @Getter private final Queue<AbstractModifyInventoryOperation> operations;

    public ModifyInventoryContext(ItemInventoryType type, ItemInventory inventory) {
        this.type = type;
        this.inventory = inventory;

        operations = new LinkedList<>();
    }

    public ModifyInventoryContext(ItemInventory inventory) {
        this.type = ItemInventoryType.EQUIP;
        this.inventory = inventory;

        operations = new LinkedList<>();
    }

    @Override
    public void add(ItemSlot item) {
        if (item instanceof ItemSlotBundle) {
            ItemSlotBundle bundle = (ItemSlotBundle) item;
            if (bundle.getNumber() < 1) bundle.setNumber((short) 1);
            if (bundle.getMaxNumber() < 1) bundle.setMaxNumber((short) 1);

            while (bundle.getNumber() > bundle.getMaxNumber()) {
                bundle.setNumber((short) (bundle.getNumber() - bundle.getMaxNumber()));
                add(Objects.requireNonNull(ItemManager.getItem(item.getTemplateId())), bundle.getMaxNumber());
            }

            ItemSlotBundle mergeable = (ItemSlotBundle) inventory.getItems().values().stream().filter(i -> {
                ItemSlotBundle b = (ItemSlotBundle) i;
                return bundle.getNumber() + b.getNumber() <= b.getMaxNumber();
            }).filter(i -> i.equals(bundle)).findFirst().orElse(null);

            if (mergeable != null) {
                int quantity = bundle.getNumber() + mergeable.getNumber();
                short maxNumber = mergeable.getMaxNumber();

                if (quantity > maxNumber) {
                    int leftOver = quantity - maxNumber;
                    bundle.setNumber((short) leftOver);
                    mergeable.setNumber(maxNumber);
                    updateQuantity(mergeable);
                    add(bundle);
                    return;
                }

                mergeable.setNumber((short) (mergeable.getNumber() + bundle.getNumber()));
                updateQuantity(mergeable);
                return;
            }
        }
        short slot = (short) IntStream.range(1, inventory.getSlotMax())
                .filter(i -> {
                    short s = (short) i;
                    return inventory.getItems().get(s) == null;
                })
                .findFirst().orElse(0);

        set(slot, item);
    }

    @Override
    public void add(ItemTemplate item, short quantity) {
        ItemSlot i = item.toItemSlot();

        if (i instanceof ItemSlotBundle) {
            ((ItemSlotBundle) i).setNumber(quantity);
        }
        add(i);
    }

    @Override
    public void set(short slot, ItemSlot item) {
        inventory.getItems().put(slot, item);
        operations.add(new AddInventoryOperation(type, slot, item));
    }

    @Override
    public void set(short slot, ItemTemplate item, short quantity) {
        ItemSlot i = item.toItemSlot();

        if (i instanceof ItemSlotBundle) {
            ((ItemSlotBundle) i).setNumber(quantity);
        }
        set(slot, i);
    }

    @Override
    public void remove(short slot) {
        inventory.getItems().remove(slot);
        operations.add(new RemoveInventoryOperation(type, slot));
    }

    @Override
    public void remove(short slot, short quantity) {
        if (inventory.getItems().get(slot) instanceof ItemSlotBundle) {
            ItemSlotBundle bundle = (ItemSlotBundle) inventory.getItems().get(slot);
            if (!ItemConstants.isRechargeableItem(bundle.getTemplateId())) {
                if (quantity > 0) {
                    bundle.setNumber((short) (bundle.getNumber() - quantity));
                    bundle.setNumber((short) Math.max((short) 0, bundle.getNumber()));

                    if (bundle.getNumber() > 0) {
                        updateQuantity(bundle);
                        return;
                    }
                }
            }
        }
        remove(slot);
    }

    @Override
    public void remove(ItemSlot item) {
        remove(inventory.getItems().entrySet()
                .stream()
                .filter(entry -> entry.getValue() == item)
                .map(Map.Entry::getKey).findFirst().get());
    }

    @Override
    public void remove(ItemSlot item, short quantity) {
        remove(inventory.getItems().entrySet()
                .stream()
                .filter(entry -> entry.getValue() == item)
                .map(Map.Entry::getKey).findFirst().get(), quantity);
    }

    @Override
    public void remove(int id, short quantity) {
        final AtomicInteger removed = new AtomicInteger(0);
        inventory.getItems().values().stream()
                .filter(i -> i.getTemplateId() == id)
                .forEach(item -> {
                    System.out.println(item);
                    if (removed.get() >= quantity) return;
                    if (item instanceof ItemSlotBundle) {
                        ItemSlotBundle bundle = (ItemSlotBundle) item;
                        if (!ItemConstants.isRechargeableItem(bundle.getTemplateId())) {
                            int difference = quantity - removed.get();
                            if (bundle.getNumber() > difference) {
                                removed.addAndGet(difference);
                                bundle.setNumber((short) (bundle.getNumber() - difference));
                                updateQuantity(bundle);
                            } else {
                                removed.addAndGet(bundle.getNumber());
                                remove(bundle);
                            }
                        }
                    } else {
                        removed.incrementAndGet();
                        remove(item);
                    }
                });
    }

    @Override
    public void move(short from, short to) {
        ItemSlot item = inventory.getItems().get(from);

        if (item instanceof ItemSlotBundle) {
            ItemSlotBundle bundle = (ItemSlotBundle) item;
            if (!ItemConstants.isRechargeableItem(bundle.getTemplateId()) &&
                    inventory.getItems().containsKey(to) &&
                    inventory.getItems().get(to) instanceof ItemSlotBundle) {
                ItemSlotBundle existing = (ItemSlotBundle) inventory.getItems().get(to);
                if (bundle.equals(existing)) {
                    int quantity = bundle.getNumber() + existing.getNumber();
                    int max = existing.getMaxNumber();

                    if (quantity > max) {
                        int left = quantity - max;

                        bundle.setNumber((short) left);
                        existing.setNumber((short) max);
                        updateQuantity(bundle);
                    } else {
                        existing.setNumber((short) quantity);
                        remove(bundle);
                    }

                    updateQuantity(existing);
                    return;
                }
            }
        }
        inventory.getItems().remove(from);
        if (inventory.getItems().containsKey(to)) {
            inventory.getItems().put(from, inventory.getItems().get(to));
        }
        inventory.getItems().put(to, item);
        operations.add(new MoveInventoryOperation(type, from, to));
    }

    @Override
    public ItemSlotBundle take(short slot, short quantity) {
        ItemSlotBundle bundle = (ItemSlotBundle) inventory.getItems().get(slot);

        ItemSlotBundle newBundle = new ItemSlotBundle();
        newBundle.setTemplateId(bundle.getTemplateId());
        newBundle.setNumber(quantity);
        newBundle.setMaxNumber(bundle.getMaxNumber());
        newBundle.setAttribute(bundle.getAttribute());
        newBundle.setTitle(bundle.getTitle());
        newBundle.setExpire(bundle.getExpire());
        newBundle.setCashItemSN(bundle.getCashItemSN());

        remove(bundle, quantity);
        return newBundle;
    }

    @Override
    public ItemSlotBundle take(ItemSlotBundle bundle, short quantity) {
        return take(bundle.getTemplateId(), quantity);
    }

    @Override
    public ItemSlotBundle take(int id, short quantity) {
        return take(inventory.getItems().entrySet()
                .stream()
                .filter(entry -> entry.getValue().getTemplateId() == id)
                .map(Map.Entry::getKey).findFirst().get(), quantity);
    }

    @Override
    public void update(short slot) {
        ItemSlot item = inventory.getItems().get(slot);

        remove(slot);
        set(slot, item);
    }

    @Override
    public void update(ItemSlot slot) {
        update(inventory.getItems().entrySet()
                .stream()
                .filter(entry -> entry.getValue() == slot)
                .map(Map.Entry::getKey).findFirst().get());
    }

    @Override
    public void encode(PacketWriter pw) {
        pw.writeByte((byte) operations.size());
        operations.forEach(operation -> operation.encode(pw));
    }

    private void updateQuantity(ItemSlot item) {
        updateQuantity(inventory.getItems().entrySet()
                .stream()
                .filter(entry -> entry.getValue() == item)
                .map(Map.Entry::getKey).findFirst().get());
    }

    private void updateQuantity(short slot) {
        operations.add(new UpdateQuantityInventoryOperation(type, slot, ((ItemSlotBundle) inventory.getItems().get(slot)).getNumber()));
    }
}
