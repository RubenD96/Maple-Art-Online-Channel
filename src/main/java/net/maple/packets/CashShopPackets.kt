package net.maple.packets

import cashshop.types.CashItemResult
import client.Client
import database.jooq.tables.Accounts
import net.database.AccountAPI.getAccountInfo
import net.database.AccountAPI.getCharacterCount
import net.database.AccountAPI.getHighestLevelOnAccount
import net.database.AccountAPI.loadNXCash
import net.maple.SendOpcode
import net.maple.packets.CharacterPackets.encodeData
import util.packet.PacketWriter
import java.util.*

object CashShopPackets {

    fun Client.sendSetCashShop() {
        val pw = PacketWriter(128)

        pw.writeHeader(SendOpcode.SET_CASH_SHOP)

        this.character.encodeData(pw)

        pw.writeBool(true) // m_bCashShopAuthorized
        pw.writeMapleString(getAccountInfo(this.accId).getValue(Accounts.ACCOUNTS.NAME)) // m_sNexonClubID

        pw.writeInt(0) // notSales
        pw.writeShort(0) // modified commodities

        pw.write(0) // discounts
        for (i in 0..89) { // best
            pw.writeInt(0) // category
            pw.writeInt(0) // gender
            pw.writeInt(0) // commoditySN
        }

        pw.writeShort(0) // DecodeStack
        pw.writeShort(0) // DecodeLimitGoods
        pw.writeShort(0) // DecodeZeroGoods

        pw.writeBool(false) // m_bEventOn
        pw.writeInt(getHighestLevelOnAccount(this.accId)) // highest character on account

        this.write(pw.createPacket())

        sendLockerData()
        loadWishList()
        sendCashData()
    }

    fun Client.sendLockerData() {
        val pw = PacketWriter(13)

        pw.writeHeader(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT)
        pw.write(CashItemResult.LOAD_LOCKER_DONE.value)

        pw.writeShort(this.locker.size)
        this.locker.forEach { it.encode(this, pw) }

        pw.writeShort(4) // storage max size
        pw.writeShort(6) // total character slots
        pw.writeShort(0) // ?
        pw.writeShort(getCharacterCount(this.accId)) // character slots used

        this.write(pw.createPacket())
    }

    fun Client.sendCashData() {
        val pw = PacketWriter(9)

        pw.writeHeader(SendOpcode.CASH_SHOP_QUERY_CASH_RESULT)

        if (this.cash == -1) loadNXCash(this)

        pw.writeInt(0) // nexon cash
        pw.writeInt(0) // maple points
        pw.writeInt(this.cash) // prepaid

        this.write(pw.createPacket())
    }

    fun Client.updateWishlist() {
        val pw = PacketWriter(43)

        pw.writeHeader(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT)
        pw.write(CashItemResult.SET_WISH_DONE.value)
        encodeWishlist(pw, this.character.wishlist)

        this.write(pw.createPacket())
    }

    fun Client.loadWishList() {
        val pw = PacketWriter(43)

        pw.writeHeader(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT)
        pw.write(CashItemResult.LOAD_WISH_DONE.value)
        encodeWishlist(pw, this.character.wishlist)

        this.write(pw.createPacket())
    }

    private fun encodeWishlist(pw: PacketWriter, wishlist: IntArray) {
        Arrays.stream(wishlist).forEach { pw.writeInt(it) }
    }
}