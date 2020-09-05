package client.interaction.shop;

import client.Character;
import client.Client;
import client.interaction.Interactable;
import client.inventory.ItemInventory;
import client.inventory.ItemInventoryType;
import client.inventory.item.templates.ItemBundleTemplate;
import client.inventory.item.templates.ItemTemplate;
import client.inventory.slots.ItemSlot;
import client.inventory.slots.ItemSlotBundle;
import constants.ItemConstants;
import managers.ItemManager;
import net.database.ShopAPI;
import net.maple.SendOpcode;
import net.maple.packets.CharacterPackets;
import org.jooq.Record;
import org.jooq.Result;
import util.packet.PacketWriter;

import java.util.LinkedHashMap;
import java.util.Map;

import static database.jooq.Tables.SHOPITEMS;

public class NPCShop implements Interactable {

    private final int id;
    private final Map<Integer, NPCShopItem> items = new LinkedHashMap<>();

    public int getId() {
        return id;
    }

    public Map<Integer, NPCShopItem> getItems() {
        return items;
    }

    public NPCShop(int id) {
        this.id = id;

        Result<Record> data = ShopAPI.INSTANCE.getShopsItems(id);
        data.forEach(record -> {
            NPCShopItem item = new NPCShopItem(record.getValue(SHOPITEMS.ITEM));
            item.setPrice(record.getValue(SHOPITEMS.PRICE));
            item.setTokenId(record.getValue(SHOPITEMS.TOKEN_ID));
            item.setTokenPrice(record.getValue(SHOPITEMS.TOKEN_PRICE));
            item.setItemPeriod(record.getValue(SHOPITEMS.PERIOD));
            item.setLevelLimited(record.getValue(SHOPITEMS.LEVEL_LIMIT));
            item.setStock(record.getValue(SHOPITEMS.STOCK));
            item.setUnitPrice(record.getValue(SHOPITEMS.UNIT_PRICE));
            item.setMaxPerSlot(record.getValue(SHOPITEMS.MAX_SLOT));
            item.setQuantity(record.getValue(SHOPITEMS.QUANTITY));
            item.setDiscountRate(record.getValue(SHOPITEMS.DISCOUNT));

            items.put(item.getId(), item);
        });
    }

    @Override
    public void open(Character chr) {
        if (chr.getNpcShop() != null) {
            chr.getClient().close(this, "Attempting to open a shop while in a shop");
            return;
        }
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.OPEN_SHOP_DLG);
        pw.writeInt(id);

        pw.writeShort(items.size());
        items.values().forEach(item -> {
            pw.writeInt(item.getId());
            pw.writeInt(item.getPrice());
            pw.write(item.getDiscountRate());
            pw.writeInt(item.getTokenId());
            pw.writeInt(item.getTokenPrice());
            pw.writeInt(item.getItemPeriod());
            pw.writeInt(item.getLevelLimited());

            if (!ItemConstants.isRechargeableItem(item.getId())) {
                pw.writeShort(item.getQuantity());
            } else {
                pw.writeDouble(item.getUnitPrice());
            }

            pw.writeShort(item.getMaxPerSlot());
        });

        chr.write(pw.createPacket());
        chr.setNpcShop(this);
    }

    @Override
    public void close(Client c) {
        c.getCharacter().setNpcShop(null);
    }

    public ShopResult buy(Character chr, short pos, int itemId, short count) {
        if (pos >= items.size()) return ShopResult.CANT_BUY_ANYMORE;

        NPCShopItem shopItem = items.get(itemId);
        if (shopItem == null) return ShopResult.BUY_UNKNOWN;
        ItemTemplate item = ItemManager.getItem(shopItem.getId());
        if (item == null) return ShopResult.BUY_UNKNOWN;

        if (shopItem.getQuantity() > 1) count = 1;
        count = (short) Math.max(count, shopItem.getMaxPerSlot());
        count = (short) Math.max(count, 1);

        if (shopItem.getPrice() > 0 && chr.getMeso() < shopItem.getPrice() * count) return ShopResult.BUY_NO_MONEY;
        if (shopItem.getTokenId() > 0 && chr.getItemQuantity(shopItem.getTokenId()) < shopItem.getTokenPrice() * count)
            return ShopResult.BUY_NO_TOKEN;
        if (chr.getLevel() < shopItem.getLevelLimited()) return ShopResult.LIMIT_LEVEL_LESS;

        ItemSlot slot = item.toItemSlot();
        if (slot instanceof ItemSlotBundle) {
            ItemSlotBundle bundle = (ItemSlotBundle) slot;
            if (ItemConstants.isRechargeableItem(slot.getTemplateId())) {
                bundle.setNumber(bundle.getMaxNumber());
            } else {
                bundle.setNumber((short) (count * shopItem.getQuantity()));
            }
        }

        if (!chr.hasInvSpace(slot)) return ShopResult.BUY_UNKNOWN;

        if (shopItem.getItemPeriod() > 0) slot.setExpire(0); // todo

        final short finalCount = count;
        if (shopItem.getPrice() > 0) {
            chr.gainMeso(-(shopItem.getPrice() * count));
        } else if (shopItem.getTokenId() > 0) {
            CharacterPackets.modifyInventory(chr,
                    i -> i.remove(id, (short) -(shopItem.getTokenPrice() * finalCount)),
                    false);
        }
        CharacterPackets.modifyInventory(chr,
                i -> i.add(item, finalCount),
                false);
        return ShopResult.BUY_SUCCESS;
    }

    public ShopResult sell(Character chr, short pos, int itemId, short count) {
        ItemInventoryType type = ItemInventoryType.values()[(itemId / 1000000) - 1];
        ItemInventory inventory = chr.getInventories().get(type);

        if (!inventory.getItems().containsKey(pos)) return ShopResult.SELL_UNKNOWN;

        ItemSlot slot = inventory.getItems().get(pos);
        ItemTemplate item = ItemManager.getItem(slot.getTemplateId());
        if (item == null) return ShopResult.SELL_UNKNOWN;
        int price = item.getSellPrice();

        if (ItemConstants.isRechargeableItem(item.getId())) {
            price += ((ItemSlotBundle) slot).getNumber() * ((ItemBundleTemplate) item).getUnitPrice();
        } else {
            price *= count;
        }

        if ((long) chr.getMeso() + (long) price > Integer.MAX_VALUE) return ShopResult.SELL_UNKNOWN;

        CharacterPackets.modifyInventory(chr, i -> i.remove(slot, count), false);
        chr.gainMeso(price);
        return ShopResult.SELL_SUCCESS;
    }

    public enum ShopResult {
        BUY_SUCCESS(0x00),
        BUY_NO_STOCK(0x01),
        BUY_NO_MONEY(0x02),
        BUY_UNKNOWN(0x03),
        SELL_SUCCESS(0x04),
        SELL_NO_STOCK(0x05),
        SELL_INCORRECT_REQUEST(0x06),
        SELL_UNKNOWN(0x07),
        RECHARGE_SUCCESS(0x08),
        RECHARGE_NO_STOCK(0x09),
        RECHARGE_NO_MONEY(0x0A),
        RECHARGE_INCORRECT_REQUEST(0x0B),
        RECHARGE_UNKNOWN(0x0C),
        BUY_NO_TOKEN(0x0D),
        LIMIT_LEVEL_LESS(0x0E),
        LIMIT_LEVEL_MORE(0x0F),
        CANT_BUY_ANYMORE(0x10),
        TRADE_BLOCKED(0x11),
        BUY_LIMIT(0x12),
        SERVER_MSG(0x13);

        private final int value;

        ShopResult(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
