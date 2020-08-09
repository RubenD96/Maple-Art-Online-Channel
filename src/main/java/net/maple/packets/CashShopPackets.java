package net.maple.packets;

import cashshop.types.CashItemResult;
import client.Client;
import database.jooq.tables.Accounts;
import net.database.AccountAPI;
import net.maple.SendOpcode;
import util.packet.PacketWriter;

public class CashShopPackets {

    public static void sendSetCashShop(Client c) {
        PacketWriter pw = new PacketWriter(128);

        pw.writeHeader(SendOpcode.SET_CASH_SHOP);

        CharacterPackets.encodeData(c.getCharacter(), pw);

        pw.writeBool(true); // m_bCashShopAuthorized
        pw.writeMapleString(AccountAPI.getAccountInfo(c.getAccId()).getValue(Accounts.ACCOUNTS.NAME)); // m_sNexonClubID

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
        pw.writeInt(AccountAPI.getHighestLevelOnAccount(c.getAccId())); // highest character on account

        c.write(pw.createPacket());

        sendLockerData(c);
        sendWishListData(c);
        sendCashData(c);
    }

    private static void sendLockerData(Client c) {
        PacketWriter pw = new PacketWriter(13);

        pw.writeHeader(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        pw.write(CashItemResult.LOAD_LOCKER_DONE.getValue());

        pw.writeShort(c.getLocker().size());
        c.getLocker().forEach(l -> l.encode(c, pw));

        pw.writeShort(4); // storage max size
        pw.writeShort(6); // total character slots
        pw.writeShort(0); // ?
        pw.writeShort(AccountAPI.getCharacterCount(c.getAccId())); // character slots used

        c.write(pw.createPacket());
    }

    private static void sendWishListData(Client c) {
        PacketWriter pw = new PacketWriter(9);

        pw.writeHeader(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT);
        pw.write(CashItemResult.LOAD_WISH_DONE.getValue());
        for (int i = 0; i < 10; i++) {
            pw.writeInt(0);
        }

        c.write(pw.createPacket());
    }

    public static void sendCashData(Client c) {
        PacketWriter pw = new PacketWriter(9);

        pw.writeHeader(SendOpcode.CASH_SHOP_QUERY_CASH_RESULT);

        if (c.getCash() == null) AccountAPI.loadNXCash(c);

        pw.writeInt(c.getCash()); // nexon cash
        pw.writeInt(0); // maple points
        pw.writeInt(0); // prepaid

        c.write(pw.createPacket());
    }
}
