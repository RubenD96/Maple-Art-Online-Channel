package net.maple.handlers.misc;

import cashshop.commodities.Commodity;
import cashshop.types.CashItemRequest;
import cashshop.types.CashItemResult;
import client.Character;
import client.Client;
import client.inventory.ItemInventory;
import client.inventory.ItemInventoryType;
import client.inventory.ModifyInventoriesContext;
import client.inventory.item.templates.ItemTemplate;
import client.inventory.slots.ItemSlotBundle;
import client.inventory.slots.ItemSlotLocker;
import managers.CommodityManager;
import managers.ItemManager;
import net.database.AccountAPI;
import net.database.CharacterAPI;
import net.database.ItemAPI;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import net.maple.packets.CashShopPackets;
import net.maple.packets.ItemPackets;
import net.server.Server;
import util.HexTool;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import static database.jooq.Tables.ACCOUNTS;

public class CashShopCashItemRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        byte type = reader.readByte();

        if (type == CashItemRequest.BUY.getValue()) {
            reader.readByte();
            sendOnBuyPacket(reader, c);
        } else if (type == CashItemRequest.GIFT.getValue()) {
            sendGift(reader, c);
        } else if (type == CashItemRequest.SET_WISH.getValue()) {
            sendSetWish(reader, c);
        } else if (type == CashItemRequest.MOVE_L_TO_S.getValue()) {
            sendOnMoveLtoS(reader, c);
        } else if (type == CashItemRequest.MOVE_S_TO_L.getValue()) {
            long id = reader.readLong();
            System.out.println("[MOVE_S_TO_L] " + id);
        } else if (type == CashItemRequest.PURCHASE_RECORD.getValue()) {
            // ignore...
        } else {
            System.err.println("[CashShopCashItemRequestHandler] Unhandled cash item operation " + HexTool.toHex(type) + " (" + type + ")\n " + HexTool.toHex(reader.getData()));
        }
    }

    private static void sendOnBuyPacket(PacketReader reader, Client c) {
        int cashType = reader.readInteger();
        if (cashType != 4) {
            System.err.println("[BUY] invalid cash type " + cashType + " - " + c.getCharacter().getName());
            failRequest(c, 2);
            return; // we don't support anything but "NX Prepaid"
        }
        int commoditySN = reader.readInteger();
        System.out.println("[BUY] " + cashType + " - " + commoditySN);

        Commodity commodity = CommodityManager.getInstance().getCommodity(commoditySN);

        if (commodity == null) {
            System.err.println("[BUY] commodity (" + commoditySN + ") is null" + " - " + c.getCharacter().getName());
            failRequest(c, 2);
            return;
        }
        if (!commodity.isOnSale()) {
            System.err.println("[BUY] commodity (" + commoditySN + ") is not for sale" + " - " + c.getCharacter().getName());
            failRequest(c, 30);
            return;
        }

        int price = commodity.getPrice();
        if (c.getCash() < price) {
            System.err.println("[BUY] not enough NX for " + commoditySN + ". Price:" + price + " NX: " + c.getCash() + " - " + c.getCharacter().getName());
            failRequest(c, 3);
            return;
        }
        if (c.getLocker().size() >= 999) {
            failRequest(c, 10);
            return; // lmao hoarding much?
        }

        ItemTemplate template = ItemManager.getItem(commodity.getItemId());
        if (template == null) {
            failRequest(c, 2);
            return;
        }
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

    private static void sendGift(PacketReader reader, Client c) {
        String spw = reader.readMapleString();
        System.out.println("spw: " + spw);
        int sn = reader.readInteger();
        System.out.println("sn: " + sn);
        reader.readByte(); // bRequestBuyOneADay
        String recipient = reader.readMapleString();
        System.out.println("recipient: " + recipient);
        String text = reader.readMapleString();
        System.out.println("text: " + text);

        if (!spw.equals(c.getPic())) {
            failRequest(c, 34);
            return;
        }
        if (text.length() > 73) {
            failRequest(c, 2);
            return;
        }
        int cid = CharacterAPI.getOfflineId(recipient);
        if (cid == -1) { // player doesn't exist
            failRequest(c, 28);
            return;
        }
        Character chr = Server.getInstance().getCharacter(cid);
        int aid = 0;
        if (chr == null) {
            aid = AccountAPI.getAccountInfoTemporary(cid).getValue(ACCOUNTS.ID);
            if (aid == c.getAccId()) {
                failRequest(c, 6);
                return;
            }
        }

        Commodity commodity = CommodityManager.getInstance().getCommodity(sn);
        if (commodity == null) {
            System.err.println("[GIFT] commodity (" + sn + ") is null" + " - " + c.getCharacter().getName());
            failRequest(c, 2);
            return;
        }
        if (!commodity.isOnSale()) {
            System.err.println("[GIFT] commodity (" + sn + ") is not for sale" + " - " + c.getCharacter().getName());
            failRequest(c, 30);
            return;
        }

        int price = commodity.getPrice();
        if (c.getCash() < price) {
            System.err.println("[GIFT] not enough NX for " + sn + ". Price:" + price + " NX: " + c.getCash() + " - " + c.getCharacter().getName());
            failRequest(c, 3);
            return;
        }

        if (chr != null) {
            if (chr.getClient().getLocker().size() >= 999) {
                failRequest(c, 10);
                return;
            }
        } else {
            if (ItemAPI.getLockerSize(aid) >= 999) {
                failRequest(c, 10);
                return;
            }
        }

        ItemTemplate template = ItemManager.getItem(commodity.getItemId());
        if (template == null) {
            failRequest(c, 2);
            return;
        }
        ItemSlotLocker slot = new ItemSlotLocker(template.toItemSlot());
        slot.setBuyCharacterName(c.getCharacter().getName());
        slot.setCommodityId(sn);

        if (chr == null) { // offline
            ItemAPI.addLockerItem(cid, aid, slot);
        } else {
            chr.getClient().getLocker().add(slot);
            if (chr.isInCashShop()) { // todo test
                CashShopPackets.sendLockerData(chr.getClient());
            }
            ItemAPI.addLockerItem(chr.getClient(), slot);
        }

        c.write(getOnGiftDonePacket(recipient, commodity));
        c.setCash(c.getCash() - price);
        CashShopPackets.sendCashData(c);
    }

    private static Packet getOnGiftDonePacket(String recipient, Commodity commodity) {
        PacketWriter pw = new PacketWriter(16);

        pw.writeHeader(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        pw.write(CashItemResult.GIFT_DONE.getValue());

        pw.writeMapleString(recipient);
        pw.writeInt(commodity.getItemId());
        pw.writeShort(commodity.getItemId());
        pw.writeInt(commodity.getPrice());

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
            failRequest(c, 2);
            return;
        }
        if (!c.getCharacter().hasInvSpace(slot.getItem())) {
            System.err.println("[MOVE_L_TO_S] No inv space " + c.getCharacter().getName());
            failRequest(c, 25);
            return;
        }
        if ((slot.getItem().getTemplateId() / 1000000) != inv) {
            System.err.println("[MOVE_L_TO_S] Wrong inventory attempt: " + (slot.getItem().getTemplateId() / 1000000) + " inv: " + inv + " " + c.getCharacter().getName());
            failRequest(c, 2);
            return;
        }
        if (inv < 1 || inv > 5) {
            System.err.println("[MOVE_L_TO_S] Invalid inv type: " + inv + " " + c.getCharacter().getName());
            failRequest(c, 2);
            return;
        }
        ItemInventory inventory = c.getCharacter().getInventories().get(ItemInventoryType.values()[inv - 1]);
        if (inventory.getItems().get(pos) != null) {
            System.err.println("[MOVE_L_TO_S] Position is not free inv: " + inv + " pos: " + pos + " " + c.getCharacter().getName());
            failRequest(c, 25);
            return;
        }
        if (inventory.getSlotMax() < pos || pos < 0) {
            System.err.println("[MOVE_L_TO_S] Position too high/low: " + inv + " pos: " + pos + " slotmax: " + inventory.getSlotMax() + " " + c.getCharacter().getName());
            failRequest(c, 25);
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

    /**
     * @param c      Client to send to
     * @param reason 1: Request timed out.\r\nPlease try again.
     *               3: You don't have enough cash.
     *               4: You can't buy someone a cash item gift if you're under 14.
     *               5: You have exceeded the allotted limit of price\r\nfor gifts.
     *               6: You cannot send a gift to your own account.\r\nPlease purchase it after logging\r\nin with the related character.
     *               7: Please confirm whether\r\nthe character's name is correct.
     *               8: This item has a gender restriction.\r\nPlease confirm the gender of the recipient.
     *               9: The gift cannot be sent because\r\nthe recipient's Inventory is full.
     *               10: Please check and see if you have exceeded\r\nthe number of cash items you can have.
     *               11: Please check and see\r\nif the name of the character is wrong,\r\nor if the item has gender restrictions.
     *               14: Please check and see if \r\nthe coupon number is right.
     *               16: This coupon has expired.
     *               17: This coupon was already used.
     *               18: This coupon can only be used at\r\nNexon-affiliated Internet Cafe's.\r\nPlease use the Nexon-affiliated Internet Cafe's.
     *               19: This coupon is a Nexon-affiliated Internet Cafe-only coupon,\r\nand it had already been used.
     *               20: This coupon is a Nexon-affiliated Internet Cafe-only coupon,\r\nand it had already been expired.
     *               21: This is the NX coupon number.\r\nRegister your coupon at www.nexon.net.
     *               22: Due to gender restrictions, the coupon \r\nis unavailable for use.
     *               23: This coupon is only for regular items, and \r\nit's unavailable to give away as a gift.
     *               24: This coupon is only for MapleStory, and\r\nit cannot be gifted to others.
     *               25: Please check if your inventory is full or not.
     *               26: This item is only available for purchase by a user at the premium service internet cafe.
     *               27: You are sending a gift to an invalid recipient.\r\nPlease check the character name and gender.
     *               28: Please check the name of the receiver.
     *               29: Items are not available for purchase\r\n at this hour.
     *               30: The item is out of stock, and therefore\r\nnot available for sale.
     *               31: You have exceeded the spending limit of NX.
     *               32: You do not have enough mesos.
     *               33: The Cash Shop is unavailable\r\nduring the beta-test phase.\r\nWe apologize for your inconvenience.
     *               34: Check your PIC password and\r\nplease try again.
     *               37: This coupon is only available to the users buying cash item for the first time.
     *               38: You have already applied for this.
     *               43: You have reached the daily maximum \r\npurchase limit for the Cash Shop.
     *               46: You have exceeded the maximum number\r\nof usage per account\for this account.\r\nPlease check the coupon for detail.
     *               48: The coupon system will be available soon.
     *               49: This item can only be used 15 days \r\nafter the account's registration.
     *               50: You do not have enough Gift Tokens \r\nin your account. Please charge your account \r\nwith Nexon Game Cards to receive \r\nGift Tokens to gift this item.
     *               51: Due to technical difficulties,\r\nthis item cannot be sent at this time.\r\nPlease try again.
     *               52: You may not gift items for \r\nit has been less than two weeks \r\nsince you first charged your account.
     *               53: Users with history of illegal activities\r\n may not gift items to others. Please make sure \r\nyour account is neither previously blocked, \r\nnor illegally charged with NX.
     *               54: Due to limitations, \r\nyou may not gift this item as this time. \r\nPlease try again later.
     *               55: You have exceeded the amount of time \r\nyou can gift items to other characters.
     *               56: This item cannot be gifted \r\ndue to technical difficulties. \r\nPlease try again later.
     *               57: You cannot transfer \r\na character under level 20.
     *               58: You cannot transfer a character \r\nto the same world it is currently in.
     *               59: You cannot transfer a character \r\ninto the new server world.
     *               60: You may not transfer out of this \r\nworld at this time.
     *               61: You cannot transfer a character into \r\na world that has no empty character slots.
     *               62: The event has either ended or\r\nthis item is not available for free testing.
     *               63: This item cannot be purchased \r\nwith MaplePoints.
     *               64: Sorry for inconvinence. \r\nplease try again.
     *               65: You can no longer purchase or gift that Item of the Day.
     *               66: This item cannot be\r\npurchased by anyone under 7.
     *               67: This item cannot be\r\nreceived by anyone under 7.
     *               68: You have reached the daily maximum \r\npurchase limit for the Cash Shop.
     *               69: NX use is restricted.\r\nPlease change your settings in the NX Security Settings menu\r\nin the Nexon Portal My Info section.
     *               default: Due to an unknown error,\r\nthe request for Cash Shop has failed.
     */
    private static void failRequest(Client c, int reason) {
        PacketWriter pw = new PacketWriter(3);

        pw.writeHeader(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        pw.write(CashItemResult.BUY_FAILED.getValue());
        pw.write(reason);

        c.write(pw.createPacket());
    }
}
