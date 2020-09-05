package client.interaction.storage;

import client.Character;
import client.Client;
import client.interaction.Interactable;
import client.inventory.ItemInventory;
import client.inventory.ItemInventoryType;
import client.inventory.ModifyInventoryContext;
import client.inventory.slots.ItemSlot;
import client.inventory.slots.ItemSlotBundle;
import client.player.DbChar;
import constants.ItemConstants;
import net.maple.SendOpcode;
import net.maple.packets.CharacterPackets;
import net.maple.packets.ItemPackets;
import util.packet.PacketWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemStorageInteraction implements Interactable {

    private final int npcId;
    private final ItemStorage storage;

    public ItemStorageInteraction(int npcId, ItemStorage storage) {
        this.npcId = npcId;
        this.storage = storage;
    }

    @Override
    public void open(Character chr) {
        if (chr.getActiveStorage() != null) {
            chr.getClient().close(this, "Attempting to open a storage while in a storage");
            return;
        }
        PacketWriter pw = new PacketWriter(18); // min size (0 items, 0 meso)

        pw.writeHeader(SendOpcode.STORAGE_RESULT);
        pw.write(StorageResult.OPEN_STORAGE_DLG.getValue());
        pw.writeInt(npcId);
        encodeItems(pw);

        chr.write(pw.createPacket());
        chr.setActiveStorage(this);
    }

    @Override
    public void close(Client c) {
        c.getCharacter().setActiveStorage(null);
    }

    public void encodeItems(PacketWriter pw) {
        encodeItems(pw, DbChar.ALL);
    }

    public void encodeItems(PacketWriter pw, DbChar flags) {
        pw.write(storage.getSlotMax());
        pw.writeLong(flags.getValue());

        if (flags.containsFlag(DbChar.MONEY)) pw.writeInt(storage.getMeso());

        Map<DbChar, ItemInventoryType> types = new HashMap<>();
        types.put(DbChar.ITEM_SLOT_EQUIP, ItemInventoryType.EQUIP);
        types.put(DbChar.ITEM_SLOT_CONSUME, ItemInventoryType.CONSUME);
        types.put(DbChar.ITEM_SLOT_INSTALL, ItemInventoryType.INSTALL);
        types.put(DbChar.ITEM_SLOT_ETC, ItemInventoryType.ETC);
        types.put(DbChar.ITEM_SLOT_CASH, ItemInventoryType.CASH);

        types.entrySet().stream()
                .filter(kv -> flags.containsFlag(kv.getKey()))
                .forEach(kv -> {
                    List<ItemSlot> items = storage.getItems().values().stream()
                            .filter(itemSlot -> ItemInventoryType.values()[itemSlot.getTemplateId() / 1000000] == kv.getValue())
                            .collect(Collectors.toList());

                    pw.write(items.size());
                    items.forEach(item -> ItemPackets.encode(item, pw));
                });
    }

    public StorageResult getItem(Character chr, ItemInventoryType type, short pos) {
        ItemSlot item = storage.getItems().values().stream()
                .filter(i -> ItemInventoryType.values()[i.getTemplateId() / 1000000 - 1] == type)
                .collect(Collectors.toList())
                .get(pos);

        if (!chr.hasInvSpace(item)) return StorageResult.GET_HAVING_ONLY_ITEM;
        /*if (chr.getMeso() < 100) return StorageResult.GET_NO_MONEY;
        chr.gainMeso(-100);*/

        CharacterPackets.modifyInventory(chr, i -> i.add(item), false);
        new ModifyInventoryContext(storage).remove(item);

        return StorageResult.GET_SUCCESS;
    }

    public StorageResult storeItem(Character chr, short pos, int id, short count) {
        ItemInventory inventory = chr.getInventories().get(ItemInventoryType.values()[id / 1000000 - 1]);
        final ItemSlot[] item = {inventory.getItems().get(pos)};

        if (storage.getItems().size() >= storage.getSlotMax()) return StorageResult.PUT_NO_SPACE;
        if (chr.getMeso() < 100) return StorageResult.PUT_NO_MONEY;
        chr.gainMeso(-100);

        CharacterPackets.modifyInventory(chr, i -> {
            if (!ItemConstants.isTreatSingly(item[0].getTemplateId()) && item[0] instanceof ItemSlotBundle) {
                ItemSlotBundle bundle = (ItemSlotBundle) item[0];
                item[0] = i.take(bundle, bundle.getNumber() < count ? bundle.getNumber() : count);
            } else {
                i.remove(item[0]);
            }
        }, false);
        new ModifyInventoryContext(storage).add(item[0]);

        return StorageResult.PUT_SUCCESS;
    }

    public StorageResult sortItems() {
        return StorageResult.SORT_ITEM;
    }

    public StorageResult transferMeso(Character chr, int amount) {
        if (amount < 0 && chr.getMeso() < amount || Integer.MAX_VALUE - chr.getMeso() < amount)
            return StorageResult.PUT_NO_MONEY;
        if (amount > 0 && storage.getMeso() < amount || Integer.MAX_VALUE - storage.getMeso() < amount)
            return StorageResult.GET_NO_MONEY;

        storage.setMeso(storage.getMeso() + -amount);
        chr.gainMeso(amount);

        return StorageResult.MONEY_SUCCESS;
    }

    public enum StorageResult {
        GET_SUCCESS(0x9),
        GET_UNKNOWN(0xA),
        GET_NO_MONEY(0xB),
        GET_HAVING_ONLY_ITEM(0xC),
        PUT_SUCCESS(0xD),
        PUT_INCORRECT_REQUEST(0xE),
        SORT_ITEM(0xF),
        PUT_NO_MONEY(0x10),
        PUT_NO_SPACE(0x11),
        PUT_UNKNOWN(0x12),
        MONEY_SUCCESS(0x13),
        MONEY_UNKNOWN(0x14),
        TRUNK_CHECK_SSN_2(0x15),
        OPEN_STORAGE_DLG(0x16),
        TRADE_BLOCKED(0x17),
        SERVER_MSG(0x18);

        private final int value;

        StorageResult(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
