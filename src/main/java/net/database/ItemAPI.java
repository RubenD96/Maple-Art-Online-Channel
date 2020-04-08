package net.database;

import client.Character;
import client.inventory.ItemInventoryType;
import client.inventory.item.templates.ItemBundleTemplate;
import client.inventory.item.templates.ItemEquipTemplate;
import client.inventory.slots.ItemSlot;
import client.inventory.slots.ItemSlotBundle;
import client.inventory.slots.ItemSlotEquip;
import managers.ItemManager;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import util.HexTool;

import java.util.*;
import java.util.stream.IntStream;

import static database.jooq.Tables.EQUIPS;
import static database.jooq.Tables.INVENTORIES;

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

        saveEquips(chr);

        IntStream.range(1, 5).forEach(i -> {
            var inv = chr.getInventories().get(ItemInventoryType.values()[i]).getItems();
            inv.forEach((slot, item) -> {
                if (item.getUuid() == null || (item.isNewItem() && uuids.stream().noneMatch(uuid -> Arrays.equals(item.getUuid(), uuid)))) { // item is new, insert new entry
                    insertNewItem(chr, i + 1, slot, (ItemSlotBundle) item);
                } else {
                    updateExistingItem(slot, (ItemSlotBundle) item);
                }
            });
        });
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

    private static void updateExistingItem(short slot, ItemSlotBundle item) {
        DatabaseCore.getConnection()
                .update(INVENTORIES)
                .set(INVENTORIES.POSITION, slot)
                .set(INVENTORIES.QUANTITY, (int) item.getNumber())
                .where(INVENTORIES.ID.eq(item.getUuid()))
                .execute();
    }

    private static void insertNewItem(Character chr, int type, short slot, ItemSlotBundle item) {
        try {
            byte[] uuid = item.getUuid();
            if (uuid == null) {
                uuid = HexTool.toBytes(UUID.randomUUID().toString().replace("-", ""));
            }
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
                            INVENTORIES.OWNER)
                    .values(uuid,
                            chr.getId(),
                            1,
                            chr.getClient().getAccId(),
                            item.getTemplateId(),
                            type,
                            slot,
                            (int) item.getNumber(),
                            null)
                    .execute();
        } catch (DataAccessException dae) {
            System.err.println("[CharacterAPI] Duplicate UUID for " + chr.getName() + " on " + item);
            dae.printStackTrace();
        }
    }

    private static void saveEquips(Character chr) {

    }

    /**
     * Loads the entire inventory of a player, should only be used on initial load
     *
     * @param chr Player to load
     */
    public static void loadInventories(Character chr) {
        loadEquips(chr);

        Result<Record> itemData = DatabaseCore.getConnection()
                .select()
                .from(INVENTORIES)
                .where(INVENTORIES.STORAGE_TYPE.eq(1))
                .and(INVENTORIES.INVENTORY_TYPE.notEqual(1)) // equip inv
                .and(INVENTORIES.CID.eq(chr.getId()))
                .fetch();

        for (Record rec : itemData) {
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
        }
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
}
