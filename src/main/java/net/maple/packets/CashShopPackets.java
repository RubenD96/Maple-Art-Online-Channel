package net.maple.packets;

import cashshop.types.CashItemResult;
import client.Client;
import database.jooq.tables.Accounts;
import net.database.AccountAPI;
import net.maple.SendOpcode;
import util.packet.PacketWriter;

import java.util.Arrays;

public class CashShopPackets {

    public static void sendSetCashShop(Client c) {
        PacketWriter pw = new PacketWriter(128);

        pw.writeHeader(SendOpcode.SET_CASH_SHOP);

        CharacterPackets.encodeData(c.getCharacter(), pw);

        pw.writeBool(true); // m_bCashShopAuthorized
        pw.writeMapleString(AccountAPI.INSTANCE.getAccountInfo(c.getAccId()).getValue(Accounts.ACCOUNTS.NAME)); // m_sNexonClubID

        pw.writeInt(0); // notSales
        pw.writeShort(0); // modified commodities

        pw.write(0); // discounts
        for (int i = 0; i < 90; i++) { // best
            pw.writeInt(0); // category
            pw.writeInt(0); // gender
            pw.writeInt(0); // commoditySN
        }

        pw.writeShort(0); // DecodeStack
        pw.writeShort(0); // DecodeLimitGoods
        pw.writeShort(0); // DecodeZeroGoods

        pw.writeBool(false); // m_bEventOn
        pw.writeInt(AccountAPI.INSTANCE.getHighestLevelOnAccount(c.getAccId())); // highest character on account

        c.write(pw.createPacket());

        sendLockerData(c);
        loadWishList(c);
        sendCashData(c);
    }

    public static void sendLockerData(Client c) {
        PacketWriter pw = new PacketWriter(13);

        pw.writeHeader(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        pw.write(CashItemResult.LOAD_LOCKER_DONE.getValue());

        pw.writeShort(c.getLocker().size());
        c.getLocker().forEach(l -> l.encode(c, pw));

        pw.writeShort(4); // storage max size
        pw.writeShort(6); // total character slots
        pw.writeShort(0); // ?
        pw.writeShort(AccountAPI.INSTANCE.getCharacterCount(c.getAccId())); // character slots used

        c.write(pw.createPacket());
    }

    public static void sendCashData(Client c) {
        PacketWriter pw = new PacketWriter(9);

        pw.writeHeader(SendOpcode.CASH_SHOP_QUERY_CASH_RESULT);

        if (c.getCash() == -1) AccountAPI.INSTANCE.loadNXCash(c);

        pw.writeInt(0); // nexon cash
        pw.writeInt(0); // maple points
        pw.writeInt(c.getCash()); // prepaid

        c.write(pw.createPacket());
    }

    public static void updateWishlist(Client c) {
        PacketWriter pw = new PacketWriter(43);

        pw.writeHeader(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        pw.write(CashItemResult.SET_WISH_DONE.getValue());
        encodeWishlist(pw, c.getCharacter().getWishlist());

        c.write(pw.createPacket());
    }

    public static void loadWishList(Client c) {
        PacketWriter pw = new PacketWriter(43);

        pw.writeHeader(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        pw.write(CashItemResult.LOAD_WISH_DONE.getValue());
        encodeWishlist(pw, c.getCharacter().getWishlist());

        c.write(pw.createPacket());
    }

    private static void encodeWishlist(PacketWriter pw, int[] wishlist) {
        Arrays.stream(wishlist).forEach(pw::writeInt);
    }
}
