package net.database;

import client.Character;
import client.Client;
import client.interaction.storage.ItemStorage;
import client.inventory.ItemInventoryType;
import client.inventory.item.templates.ItemBundleTemplate;
import client.inventory.item.templates.ItemEquipTemplate;
import client.inventory.item.templates.ItemTemplate;
import client.inventory.slots.ItemSlot;
import client.inventory.slots.ItemSlotBundle;
import client.inventory.slots.ItemSlotEquip;
import client.inventory.slots.ItemSlotLocker;
import database.jooq.tables.records.InventoriesRecord;
import managers.ItemManager;
import org.jooq.DeleteConditionStep;
import org.jooq.DeleteUsingStep;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import util.HexTool;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static database.jooq.Tables.*;

public class ItemAPI {

    /**
     * Saves the player's inventory.
     * Only insert if an new item has entered the player's inventory.
     * Only update if an item already existed and has been changed.
     *
     * @param chr The player to save
     */
    public static void saveInventories(Character chr) {
        Set<byte[]> uuids = deleteOldItems(chr);

        // inventories
        IntStream.range(0, 5).forEach(i -> {
            var type = ItemInventoryType.values()[i];
            var inv = chr.getInventories().get(type).getItems();
            inv.forEach((slot, item) -> {
                updateItem(chr, item, uuids, slot, type, 1);
            });
        });

        // storage
        updateStorageStats(chr.getClient());
        chr.getClient().getStorage().getItems().forEach((slot, item) -> {
            updateItem(chr, item, uuids, slot, ItemInventoryType.values()[item.getTemplateId() / 1000000 - 1], 2);
        });
    }

    private static void updateItem(Character chr, ItemSlot item, Set<byte[]> uuids, Short slot, ItemInventoryType invType, int storageType) {
        if (item.getUuid() == null || (item.isNewItem() && uuids.stream().noneMatch(uuid -> Arrays.equals(item.getUuid(), uuid)))) { // item is new, insert new entry
            insertNewItem(chr, storageType, invType.getType(), slot, item, null);
            if (invType == ItemInventoryType.EQUIP) {
                insertNewEquip((ItemSlotEquip) item);
            }
        } else {
            updateExistingItem(slot, item, storageType);
            if (invType == ItemInventoryType.EQUIP) {
                updateExistingEquip((ItemSlotEquip) item);
            }
        }
    }

    /**
     * Update meso and storage size
     *
     * @param c account to update
     */
    private static void updateStorageStats(Client c) {
        ItemStorage storage = c.getStorage();
        DatabaseCore.getConnection()
                .update(STORAGES)
                .set(STORAGES.SIZE, storage.getSlotMax())
                .set(STORAGES.MESO, storage.getMeso())
                .where(STORAGES.AID.eq(c.getAccId()))
                .execute();
    }

    /**
     * Deletes old items that are no longer in the player's inventory
     *
     * @param chr The player to remove items from
     */
    private static Set<byte[]> deleteOldItems(Character chr) {
        Set<byte[]> toDelete = new HashSet<>();
        Set<byte[]> keep = new HashSet<>();
        var res = DatabaseCore.getConnection()
                .select(INVENTORIES.ID)
                .from(INVENTORIES)
                .where(INVENTORIES.CID.eq(chr.getId()))
                .fetch();
        for (var id : res) {
            byte[] uuid = id.getValue(INVENTORIES.ID);
            toDelete.add(uuid);
            chr.getInventories().values().forEach(inv -> {
                if (inv.getItems().values().stream().anyMatch(item -> Arrays.equals(item.getUuid(), uuid))) {
                    keep.add(uuid);
                    toDelete.remove(uuid);
                }
            });
            if (chr.getClient().getLocker().stream().anyMatch(item -> Arrays.equals(item.getItem().getUuid(), uuid))) {
                keep.add(uuid);
                toDelete.remove(uuid);
            }
            if (chr.getClient().getStorage().getItems().values().stream().anyMatch(item -> Arrays.equals(item.getUuid(), uuid))) {
                keep.add(uuid);
                toDelete.remove(uuid);
            }
        }

        toDelete.forEach(ItemAPI::deleteItemByUUID);
        return keep;
    }

    public static void deleteItemByUUID(byte[] uuid) {
        DatabaseCore.getConnection()
                .deleteFrom(INVENTORIES)
                .where(INVENTORIES.ID.eq(uuid))
                .execute();
    }

    private static void updateExistingItem(short slot, ItemSlot item, int storageType) {
        int quantity = item instanceof ItemSlotBundle ? ((ItemSlotBundle) item).getNumber() : 1;
        DatabaseCore.getConnection()
                .update(INVENTORIES)
                .set(INVENTORIES.STORAGE_TYPE, storageType)
                .set(INVENTORIES.POSITION, slot)
                .set(INVENTORIES.QUANTITY, quantity)
                .where(INVENTORIES.ID.eq(item.getUuid()))
                .execute();
    }

    private static void updateExistingEquip(ItemSlotEquip equip) {
        DatabaseCore.getConnection()
                .update(EQUIPS)
                .set(EQUIPS.SLOTS, equip.getRUC())
                .set(EQUIPS.STR, equip.getSTR())
                .set(EQUIPS.DEX, equip.getDEX())
                .set(EQUIPS.INT, equip.getINT())
                .set(EQUIPS.LUK, equip.getLUK())
                .set(EQUIPS.HP, equip.getMaxHP())
                .set(EQUIPS.MP, equip.getMaxMP())
                .set(EQUIPS.PAD, equip.getPAD())
                .set(EQUIPS.MAD, equip.getMAD())
                .set(EQUIPS.PDD, equip.getPDD())
                .set(EQUIPS.MDD, equip.getMDD())
                .set(EQUIPS.ACC, equip.getACC())
                .set(EQUIPS.EVA, equip.getEVA())
                .set(EQUIPS.SPEED, equip.getSpeed())
                .set(EQUIPS.JUMP, equip.getJump())
                .set(EQUIPS.CRAFT, equip.getCraft())
                .set(EQUIPS.DURABILITY, equip.getDurability())
                .where(EQUIPS.ITEMID.eq(equip.getUuid()))
                .execute();
    }

    private static void insertNewItem(Character chr, int storageType, int type, short slot, ItemSlot item, String giftFrom) {
        insertNewItem(chr.getId(), chr.getClient().getAccId(), storageType, type, slot, item, giftFrom);
    }

    private static void insertNewItem(int cid, int aid, int storageType, int type, short slot, ItemSlot item, String giftFrom) {
        try {
            byte[] uuid = item.getUuid();
            if (uuid == null) {
                uuid = HexTool.toBytes(UUID.randomUUID().toString().replace("-", ""));
                item.setUuid(uuid);
            }
            int quantity = item instanceof ItemSlotBundle ? ((ItemSlotBundle) item).getNumber() : 1;
            DatabaseCore.getConnection()
                    .insertInto(INVENTORIES,
                            INVENTORIES.ID,
                            INVENTORIES.CID,
                            INVENTORIES.STORAGE_TYPE,
                            INVENTORIES.AID,
                            INVENTORIES.ITEMID,
                            INVENTORIES.INVENTORY_TYPE,
                            INVENTORIES.POSITION,
                            INVENTORIES.QUANTITY,
                            INVENTORIES.OWNER,
                            INVENTORIES.GIFTFROM)
                    .values(uuid,
                            cid,
                            storageType,
                            aid,
                            item.getTemplateId(),
                            type,
                            slot,
                            quantity,
                            null,
                            giftFrom)
                    .execute();
        } catch (DataAccessException dae) {
            System.err.println("[ItemAPI] Duplicate UUID for " + cid + " on " + item);
            dae.printStackTrace();
        }
    }

    private static void insertNewEquip(ItemSlotEquip equip) {
        DatabaseCore.getConnection()
                .insertInto(EQUIPS,
                        EQUIPS.ITEMID,
                        EQUIPS.SLOTS,
                        EQUIPS.LEVEL,
                        EQUIPS.STR,
                        EQUIPS.DEX,
                        EQUIPS.INT,
                        EQUIPS.LUK,
                        EQUIPS.HP,
                        EQUIPS.MP,
                        EQUIPS.PAD,
                        EQUIPS.MAD,
                        EQUIPS.PDD,
                        EQUIPS.MDD,
                        EQUIPS.ACC,
                        EQUIPS.EVA,
                        EQUIPS.SPEED,
                        EQUIPS.JUMP,
                        EQUIPS.CRAFT,
                        EQUIPS.DURABILITY)
                .values(equip.getUuid(),
                        equip.getRUC(),
                        equip.getLevel(),
                        equip.getSTR(),
                        equip.getDEX(),
                        equip.getINT(),
                        equip.getLUK(),
                        equip.getMaxHP(),
                        equip.getMaxMP(),
                        equip.getPAD(),
                        equip.getMAD(),
                        equip.getPDD(),
                        equip.getMDD(),
                        equip.getACC(),
                        equip.getEVA(),
                        equip.getSpeed(),
                        equip.getJump(),
                        equip.getCraft(),
                        equip.getDurability())
                .execute();
    }

    /**
     * Loads the entire inventory of a player, should only be used on initial load
     *
     * @param chr Player to load
     */
    public static void loadInventories(Character chr) {
        deleteBrokenEquips(chr);
        loadEquips(chr);
        loadLocker(chr.getClient());
        loadStorage(chr.getClient());

        Result<Record> itemData = DatabaseCore.getConnection()
                .select()
                .from(INVENTORIES)
                .where(INVENTORIES.STORAGE_TYPE.eq(1))
                .and(INVENTORIES.INVENTORY_TYPE.notEqual(1)) // equip inv
                .and(INVENTORIES.CID.eq(chr.getId()))
                .fetch();

        itemData.forEach(rec -> {
            Map<Short, ItemSlot> items = chr.getInventories()
                    .get(ItemInventoryType.values()[rec.getValue(INVENTORIES.INVENTORY_TYPE) - 1])
                    .getItems();
            ItemBundleTemplate template = (ItemBundleTemplate) ItemManager.getItem(rec.getValue(INVENTORIES.ITEMID));
            if (template != null) {
                ItemSlotBundle bundle = template.toItemSlot();
                if (rec.getValue(INVENTORIES.QUANTITY) > 1) {
                    bundle.setNumber(rec.getValue(INVENTORIES.QUANTITY).shortValue());
                }
                bundle.setUuid(rec.getValue(INVENTORIES.ID));
                bundle.setNewItem(false);
                items.put(rec.getValue(INVENTORIES.POSITION), bundle);
            }
        });
    }

    private static void loadStorage(Client c) {
        Record storage = DatabaseCore.getConnection()
                .select()
                .from(STORAGES)
                .where(STORAGES.AID.eq(c.getAccId()))
                .fetchOne();
        ItemStorage itemStorage;
        if (storage == null) {
            DatabaseCore.getConnection()
                    .insertInto(STORAGES, STORAGES.AID)
                    .values(c.getAccId())
                    .execute();
            itemStorage = new ItemStorage((short) 4, 0);
        } else {
            itemStorage = new ItemStorage(storage.getValue(STORAGES.SIZE), storage.getValue(STORAGES.MESO));
        }
        c.setStorage(itemStorage);

        Result<Record> itemData = DatabaseCore.getConnection()
                .select()
                .from(INVENTORIES)
                .where(INVENTORIES.STORAGE_TYPE.eq(2))
                .and(INVENTORIES.AID.eq(c.getAccId()))
                .fetch();

        itemData.forEach(rec -> {
            ItemTemplate template = ItemManager.getItem(rec.getValue(INVENTORIES.ITEMID));
            if (template != null) {
                ItemSlot item = template.toItemSlot();
                if (rec.getValue(INVENTORIES.QUANTITY) > 1) {
                    ((ItemSlotBundle) item).setNumber(rec.getValue(INVENTORIES.QUANTITY).shortValue());
                }
                item.setUuid(rec.getValue(INVENTORIES.ID));
                item.setNewItem(false);
                c.getStorage().getItems().put(rec.getValue(INVENTORIES.POSITION), item);
            }
        });
    }

    /**
     * If an equip happens to appear in the DB without any stats, it could cause problems.
     * This method detects equips in the DB that have no stats and removes them.
     *
     * @param chr The player to check
     */
    private static void deleteBrokenEquips(Character chr) {
        Result<Record> itemData = DatabaseCore.getConnection()
                .select()
                .from(INVENTORIES)
                .where(INVENTORIES.INVENTORY_TYPE.eq(1)) // equip inv
                .and(INVENTORIES.CID.eq(chr.getId()))
                .fetch();
        itemData.forEach(rec -> {
            Record match = DatabaseCore.getConnection()
                    .select()
                    .from(EQUIPS)
                    .where(EQUIPS.ITEMID.eq(rec.getValue(INVENTORIES.ID)))
                    .fetchOne();
            if (match == null) {
                System.err.println("[ItemAPI] Broken equip found for " + chr.getName() + " (" + rec.getValue(INVENTORIES.ITEMID) + ")");
                deleteItemByUUID(rec.getValue(INVENTORIES.ID));
            }
        });
    }

    /**
     * Separate from loadInventories because of equip stats
     *
     * @param chr Player to load
     */
    private static void loadEquips(Character chr) {
        Map<Short, ItemSlot> equips = chr.getInventories().get(ItemInventoryType.EQUIP).getItems();
        Result<Record> equipData = DatabaseCore.getConnection()
                .select()
                .from(EQUIPS)
                .join(INVENTORIES)
                .onKey()
                .where(INVENTORIES.STORAGE_TYPE.eq(1))
                .and(INVENTORIES.INVENTORY_TYPE.eq(1)) // equip inv
                .and(INVENTORIES.CID.eq(chr.getId()))
                .fetch();
        for (Record rec : equipData) {
            ItemEquipTemplate template = ((ItemEquipTemplate) ItemManager.getItem(rec.getValue(INVENTORIES.ITEMID)));
            if (template != null) {
                ItemSlotEquip equip = template.fromDbToSlot(
                        rec.getValue(EQUIPS.SLOTS),
                        rec.getValue(EQUIPS.STR),
                        rec.getValue(EQUIPS.DEX),
                        rec.getValue(EQUIPS.INT),
                        rec.getValue(EQUIPS.LUK),
                        rec.getValue(EQUIPS.HP),
                        rec.getValue(EQUIPS.MP),
                        rec.getValue(EQUIPS.PAD),
                        rec.getValue(EQUIPS.MAD),
                        rec.getValue(EQUIPS.PDD),
                        rec.getValue(EQUIPS.MDD),
                        rec.getValue(EQUIPS.ACC),
                        rec.getValue(EQUIPS.EVA),
                        rec.getValue(EQUIPS.SPEED),
                        rec.getValue(EQUIPS.JUMP),
                        rec.getValue(EQUIPS.CRAFT),
                        rec.getValue(EQUIPS.DURABILITY)
                );
                equip.setUuid(rec.getValue(INVENTORIES.ID));
                equip.setNewItem(false);

                equips.put(rec.getValue(INVENTORIES.POSITION), equip);
            }
        }
    }

    /**
     * Used for offline gift sending check
     *
     * @param aid account id
     * @return size of account locker
     */
    public static int getLockerSize(int aid) {
        return DatabaseCore.getConnection()
                .fetchCount(DatabaseCore.getConnection()
                        .select().from(INVENTORIES)
                        .where(INVENTORIES.STORAGE_TYPE.eq(3))
                        .and(INVENTORIES.AID.eq(aid)));
    }

    public static int getAvailableLockerSlot(int aid) {
        AtomicInteger highest = new AtomicInteger(1);
        DatabaseCore.getConnection()
                .select().from(INVENTORIES)
                .where(INVENTORIES.STORAGE_TYPE.eq(3))
                .and(INVENTORIES.AID.eq(aid))
                .fetch()
                .forEach(item -> {
                    int slot = item.getValue(INVENTORIES.POSITION);
                    if (highest.get() < slot) {
                        highest.set(slot + 1);
                    }
                });
        return highest.get();
    }

    /**
     * Cashshop inventory
     *
     * @param c client
     */
    private static void loadLocker(Client c) {
        Result<Record> locker = DatabaseCore.getConnection()
                .select().from(INVENTORIES)
                .where(INVENTORIES.STORAGE_TYPE.eq(3))
                .and(INVENTORIES.AID.eq(c.getAccId()))
                .fetch();

        List<Integer> toRemove = new ArrayList<>();
        Map<Short, ItemSlotLocker> toAdd = new TreeMap<>();
        locker.forEach(item -> {
            int itemId = item.getValue(INVENTORIES.ITEMID);
            ItemTemplate template = ItemManager.getItem(itemId);
            String giftFrom = item.getValue(INVENTORIES.GIFTFROM);
            if (template != null) { // mmhm
                ItemSlotLocker lockerItem = new ItemSlotLocker(template.toItemSlot());
                lockerItem.getItem().setUuid(item.getValue(INVENTORIES.ID));
                lockerItem.setBuyCharacterName(giftFrom == null ? "" : giftFrom);
                toAdd.put(item.getValue(INVENTORIES.POSITION), lockerItem);
            } else {
                toRemove.add(itemId);
            }
        });

        c.getLocker().addAll(toAdd.values());

        if (!toRemove.isEmpty()) {
            DeleteUsingStep<InventoriesRecord> del = DatabaseCore.getConnection().deleteFrom(INVENTORIES);
            DeleteConditionStep<InventoriesRecord> conditionStep = del.where(INVENTORIES.ITEMID.eq(toRemove.get(0)));
            toRemove.remove(0); // this is pretty derpy...
            for (Integer r : toRemove) {
                conditionStep = conditionStep.or(INVENTORIES.ITEMID.eq(r));
            }
            del.execute();
        }
    }

    public static void addLockerItem(int cid, int aid, ItemSlotLocker item) {
        int type = item.getItem().getTemplateId() / 1000000;
        insertNewItem(cid, aid, 3, type, (short) getAvailableLockerSlot(aid), item.getItem(), item.getBuyCharacterName());

        if (ItemInventoryType.values()[type - 1] == ItemInventoryType.EQUIP) {
            insertNewEquip((ItemSlotEquip) item.getItem());
        }
    }

    public static void addLockerItem(Client c, ItemSlotLocker item) {
        int type = item.getItem().getTemplateId() / 1000000;
        insertNewItem(c.getCharacter(), 3, type, (short) (c.getLocker().indexOf(item) + 1), item.getItem(), item.getBuyCharacterName());

        if (ItemInventoryType.values()[type - 1] == ItemInventoryType.EQUIP) {
            insertNewEquip((ItemSlotEquip) item.getItem());
        }
    }

    public static void moveLockerToStorage(ItemSlotLocker item, short slot) {
        DatabaseCore.getConnection()
                .update(INVENTORIES)
                .set(INVENTORIES.STORAGE_TYPE, 1)
                .set(INVENTORIES.POSITION, slot)
                .where(INVENTORIES.ID.eq(item.getItem().getUuid()))
                .execute();
    }
}
