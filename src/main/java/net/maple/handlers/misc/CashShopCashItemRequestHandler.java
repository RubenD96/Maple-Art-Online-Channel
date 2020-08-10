package net.maple.handlers.misc;

import cashshop.commodities.Commodity;
import cashshop.types.CashItemRequest;
import cashshop.types.CashItemResult;
import client.Client;
import client.inventory.ItemInventory;
import client.inventory.ItemInventoryType;
import client.inventory.ModifyInventoriesContext;
import client.inventory.item.templates.ItemTemplate;
import client.inventory.slots.ItemSlotLocker;
import managers.CommodityManager;
import managers.ItemManager;
import net.database.ItemAPI;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import net.maple.packets.CashShopPackets;
import net.maple.packets.ItemPackets;
import util.HexTool;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class CashShopCashItemRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        byte type = reader.readByte();

        if (type == CashItemRequest.BUY.getValue()) {
            reader.readByte();
            sendOnBuyPacket(reader, c);
        } else if (type == CashItemRequest.SET_WISH.getValue()) {
            sendSetWish(reader, c);
        } else if (type == CashItemRequest.MOVE_L_TO_S.getValue()) {
            sendOnMoveLtoS(reader, c);
        } else if (type == CashItemRequest.MOVE_S_TO_L.getValue()) {
            long id = reader.readLong();
            System.out.println("[MOVE_S_TO_L] " + id);
        } else {
            System.err.println("[CashShopCashItemRequestHandler] Unhandled cash item operation " + HexTool.toHex(type) + " (" + type + ")\n " + HexTool.toHex(reader.getData()));
        }
    }

    private static void sendOnBuyPacket(PacketReader reader, Client c) {
        int cashType = reader.readInteger();
        if (cashType != 1) return; // we don't support anything but "NX Credit"
        int commoditySN = reader.readInteger();
        System.out.println("[BUY] " + cashType + " - " + commoditySN);

        Commodity commodity = CommodityManager.getInstance().getCommodity(commoditySN);

        if (commodity == null) return;
        if (!commodity.isOnSale()) return;

        int price = commodity.getPrice();
        if (c.getCash() < price) return;
        if (c.getLocker().size() >= 999) return; // lmao hoarding much?

        ItemTemplate template = ItemManager.getItem(commodity.getItemId());
        if (template == null) return;
        ItemSlotLocker slot = new ItemSlotLocker(template.toItemSlot());
        slot.setBuyCharacterName("");
        slot.setCommodityId(commoditySN);

        c.getLocker().add(slot);
        c.write(getOnBuyPacket(slot, c));
        ItemAPI.addLockerItem(c, slot);

        c.setCash(c.getCash() - price);
        CashShopPackets.sendCashData(c);
    }

    private static Packet getOnBuyPacket(ItemSlotLocker slot, Client c) {
        PacketWriter pw = new PacketWriter(58);

        pw.writeHeader(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        pw.write(CashItemResult.BUY_DONE.getValue());
        slot.encode(c, pw);

        return pw.createPacket();
    }

    private static void sendOnMoveLtoS(PacketReader reader, Client c) {
        long sn = reader.readLong();
        byte inv = reader.readByte();
        short pos = reader.readShort();

        ItemSlotLocker slot = c.getLocker().stream()
                .filter(i -> i.getItem().getCashItemSN() == sn)
                .findFirst().orElse(null);

        if (slot == null) {
            System.err.println("[MOVE_L_TO_S] Slot is null " + c.getCharacter().getName());
            return;
        }
        if (!c.getCharacter().hasInvSpace(slot.getItem())) {
            System.err.println("[MOVE_L_TO_S] No inv space " + c.getCharacter().getName());
            return;
        }
        if ((slot.getItem().getTemplateId() / 1000000) != inv) {
            System.err.println("[MOVE_L_TO_S] Wrong inventory attempt: " + (slot.getItem().getTemplateId() / 1000000) + " inv: " + inv + " " + c.getCharacter().getName());
            return;
        }
        if (inv < 1 || inv > 5) {
            System.err.println("[MOVE_L_TO_S] Invalid inv type: " + inv + " " + c.getCharacter().getName());
            return;
        }
        ItemInventory inventory = c.getCharacter().getInventories().get(ItemInventoryType.values()[inv - 1]);
        if (inventory.getItems().get(pos) != null) {
            System.err.println("[MOVE_L_TO_S] Position is not free inv: " + inv + " pos: " + pos + " " + c.getCharacter().getName());
            return;
        }
        if (inventory.getSlotMax() < pos || pos < 0) {
            System.err.println("[MOVE_L_TO_S] Position too high/low: " + inv + " pos: " + pos + " slotmax: " + inventory.getSlotMax() + " " + c.getCharacter().getName());
            return;
        }

        ModifyInventoriesContext context = new ModifyInventoriesContext(c.getCharacter().getInventories());

        c.getLocker().remove(slot);
        context.add(slot.getItem());

        /*short pos = c.getCharacter().getInventories().values().stream()
                .flatMap(i -> i.getItems().entrySet().stream())
                .filter(i -> i.getValue() == slot.getItem())
                .findFirst().get().getKey();*/

        c.write(getOnMoveLtoSDonePacket(slot, c, pos));
        ItemAPI.moveLockerToStorage(slot, pos);
    }

    private static Packet getOnMoveLtoSDonePacket(ItemSlotLocker slot, Client c, short pos) {
        PacketWriter pw = new PacketWriter(58);

        pw.writeHeader(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        pw.write(CashItemResult.MOVE_LTOS_DONE.getValue());
        pw.writeShort(pos);
        ItemPackets.encode(slot.getItem(), pw);

        return pw.createPacket();
    }

    private static void sendSetWish(PacketReader reader, Client c) {
        c.getCharacter().setWishlist(new int[10]);
        for (int i = 0; i < 10; i++) {
            int sn = reader.readInteger();
            if (sn == 0) {
                continue;
            }

            Commodity commodity = CommodityManager.getInstance().getCommodity(sn);

            if (commodity == null) {
                System.err.println("[SET_WISH] commodity is null");
                continue;
            }
            if (!commodity.isOnSale()) {
                System.err.println("[SET_WISH] commodity is not on sale");
                continue;
            }

            c.getCharacter().getWishlist()[i] = sn;
        }
        CashShopPackets.updateWishlist(c);
    }
}
