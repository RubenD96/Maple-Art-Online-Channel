package net.maple.handlers.misc

import cashshop.commodities.Commodity
import cashshop.types.CashItemRequest
import cashshop.types.CashItemResult
import client.Character
import client.Client
import client.inventory.ItemInventoryType
import client.inventory.ModifyInventoriesContext
import client.inventory.item.slots.ItemSlotLocker
import database.jooq.Tables
import managers.CommodityManager.getCommodity
import managers.ItemManager.getItem
import net.database.AccountAPI.getAccountInfoTemporary
import net.database.CharacterAPI.getOfflineId
import net.database.ItemAPI.addLockerItem
import net.database.ItemAPI.getLockerSize
import net.database.ItemAPI.moveLockerToStorage
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import net.maple.packets.CashShopPackets.sendCashData
import net.maple.packets.CashShopPackets.sendLockerData
import net.maple.packets.CashShopPackets.updateWishlist
import net.maple.packets.ItemPackets.encode
import net.server.Server.getCharacter
import util.HexTool.toHex
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter

class CashShopCashItemRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val type = reader.readByte()

        when {
            type.toInt() == CashItemRequest.BUY.value -> {
                reader.readByte()
                sendOnBuyPacket(reader, c)
            }
            type.toInt() == CashItemRequest.GIFT.value -> {
                sendGift(reader, c)
            }
            type.toInt() == CashItemRequest.SET_WISH.value -> {
                sendSetWish(reader, c)
            }
            type.toInt() == CashItemRequest.MOVE_L_TO_S.value -> {
                sendOnMoveLtoS(reader, c)
            }
            type.toInt() == CashItemRequest.MOVE_S_TO_L.value -> {
                val id = reader.readLong()
                println("[MOVE_S_TO_L] $id")
            }
            type.toInt() == CashItemRequest.FRIENDSHIP.value -> {
                sendOnBuyFriendship(reader, c)
            }
            type.toInt() == CashItemRequest.PURCHASE_RECORD.value -> {
                // ignore...
            }
            else -> {
                System.err.println("[CashShopCashItemRequestHandler] Unhandled cash item operation 0x${toHex(type)} ($type) ${toHex(reader.data)}")
            }
        }
    }

    override fun validateState(c: Client): Boolean {
        return true
    }

    companion object {
        private fun sendOnBuyPacket(reader: PacketReader, c: Client) {
            val cashType = reader.readInteger()

            if (cashType != 4) {
                System.err.println("[BUY] invalid cash type $cashType - ${c.character.name}")
                failRequest(c, 2)
                return  // we don't support anything but "NX Prepaid"
            }

            val commoditySN = reader.readInteger()
            println("[BUY] $cashType - $commoditySN")
            val commodity = getCommodity(commoditySN) ?: return run {
                System.err.println("""[BUY] commodity ($commoditySN) is null - ${c.character.name}""")
                failRequest(c, 2)
            }

            if (!commodity.isOnSale) {
                System.err.println("[BUY] commodity ($commoditySN) is not for sale - ${c.character.name}")
                failRequest(c, 30)
                return
            }

            val price = commodity.price
            if (c.cash < price) {
                System.err.println("[BUY] not enough NX for $commoditySN. Price:$price NX: ${c.cash} - ${c.character.name}")
                failRequest(c, 3)
                return
            }

            if (c.locker.size >= 999) {
                failRequest(c, 10)
                return  // lmao hoarding much?
            }

            val template = getItem(commodity.itemId)

            val slot = ItemSlotLocker(template.toItemSlot())
            slot.buyCharacterName = ""
            slot.commodityId = commoditySN

            c.locker.add(slot)
            c.write(getOnBuyPacket(slot, c))
            addLockerItem(c, slot)

            c.cash = c.cash - price
            c.sendCashData()
        }

        private fun getOnBuyPacket(slot: ItemSlotLocker, c: Client): Packet {
            val pw = PacketWriter(58)
            pw.writeHeader(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT)
            pw.write(CashItemResult.BUY_DONE.value)
            slot.encode(c, pw)
            return pw.createPacket()
        }

        private fun giftChecks(c: Client, spw: String, text: String, cid: Int): Boolean {
            if (spw != c.pic) {
                failRequest(c, 34)
                return false
            }

            if (text.length > 73) {
                failRequest(c, 2)
                return false
            }

            if (cid == -1) { // player doesn't exist
                failRequest(c, 28)
                return false
            }
            return true
        }

        private fun commodityBuyChecks(c: Client, commodity: Commodity?, sn: Int, chr: Character?, aid: Int): Boolean {
            if (commodity == null) {
                System.err.println("[GIFT] commodity ($sn) is null - ${c.character.name}")
                failRequest(c, 2)
                return false
            }

            if (!commodity.isOnSale) {
                System.err.println("[GIFT] commodity ($sn) is not for sale - ${c.character.name}")
                failRequest(c, 30)
                return false
            }

            val price = commodity.price
            if (c.cash < price) {
                System.err.println("[GIFT] not enough NX for $sn. Price:$price NX: ${c.cash} - ${c.character.name}")
                failRequest(c, 3)
                return false
            }

            if (chr != null) {
                if (chr.client.locker.size >= 999) {
                    failRequest(c, 10)
                    return false
                }
            } else {
                if (getLockerSize(aid) >= 999) {
                    failRequest(c, 10)
                    return false
                }
            }
            return true
        }

        private fun getOfflineAccountId(c: Client, chr: Character?, cid: Int): Int {
            var aid: Int

            return chr?.run {
                aid = chr.client.accId
                aid
            } ?: run {
                aid = getAccountInfoTemporary(cid).getValue(Tables.ACCOUNTS.ID)
                if (aid == c.accId) {
                    failRequest(c, 6)
                    return -1
                }
                aid
            }
        }

        private fun sendGift(reader: PacketReader, c: Client) {
            val spw = reader.readMapleString()
            val sn = reader.readInteger()

            reader.readByte() // bRequestBuyOneADay

            val giftTo = reader.readMapleString()
            val text = reader.readMapleString()
            val cid = getOfflineId(giftTo)

            if (!giftChecks(c, spw, text, cid)) return

            val chr = getCharacter(cid)
            val aid = getOfflineAccountId(c, chr, cid)
            if (aid == -1) return

            val commodity = getCommodity(sn) ?: return failRequest(c, 2)
            if (!commodityBuyChecks(c, commodity, sn, chr, aid)) return

            val template = getItem(commodity.itemId)

            val slot = ItemSlotLocker(template.toItemSlot())
            slot.buyCharacterName = c.character.name
            slot.commodityId = sn

            chr?.let {
                c.locker.add(slot)
                if (chr.isInCashShop) { // todo test
                    c.sendLockerData()
                }
                addLockerItem(c, slot)
            } ?: addLockerItem(cid, aid, slot)

            c.write(getOnGiftDonePacket(giftTo, commodity))
            c.cash = c.cash - commodity.price
            c.sendCashData()
        }

        private fun getOnGiftDonePacket(recipient: String, commodity: Commodity): Packet {
            val pw = PacketWriter(16)

            pw.writeHeader(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT)
            pw.write(CashItemResult.GIFT_DONE.value)
            pw.writeMapleString(recipient)
            pw.writeInt(commodity.itemId)
            pw.writeShort(commodity.itemId)
            pw.writeInt(commodity.price)

            return pw.createPacket()
        }

        private fun sendOnMoveLtoS(reader: PacketReader, c: Client) {
            val sn = reader.readLong()
            val inv = reader.readByte()
            val pos = reader.readShort()

            val slot = c.locker.stream()
                    .filter { i: ItemSlotLocker -> i.item.cashItemSN == sn }
                    .findFirst().orElse(null) ?: return run {
                System.err.println("[MOVE_L_TO_S] Slot is null " + c.character.name)
                failRequest(c, 2)
            }

            if (!c.character.hasInvSpace(slot.item)) {
                System.err.println("[MOVE_L_TO_S] No inv space " + c.character.name)
                failRequest(c, 25)
                return
            }

            if (slot.item.templateId / 1000000 != inv.toInt()) {
                System.err.println("[MOVE_L_TO_S] Wrong inventory attempt: " + slot.item.templateId / 1000000 + " inv: " + inv + " " + c.character.name)
                failRequest(c, 2)
                return
            }

            if (inv < 1 || inv > 5) {
                System.err.println("[MOVE_L_TO_S] Invalid inv type: " + inv + " " + c.character.name)
                failRequest(c, 2)
                return
            }

            val inventory = c.character.getInventory(ItemInventoryType.values()[inv - 1])
            if (inventory.items[pos] != null) {
                System.err.println("[MOVE_L_TO_S] Position is not free inv: " + inv + " pos: " + pos + " " + c.character.name)
                failRequest(c, 25)
                return
            }

            if (inventory.slotMax < pos || pos < 0) {
                System.err.println("[MOVE_L_TO_S] Position too high/low: " + inv + " pos: " + pos + " slotmax: " + inventory.slotMax + " " + c.character.name)
                failRequest(c, 25)
                return
            }

            val context = ModifyInventoriesContext(c.character.allInventories)
            c.locker.remove(slot)
            context.add(slot.item)

            /*short pos = c.getCharacter().getInventories().values().stream()
                .flatMap(i -> i.getItems().entrySet().stream())
                .filter(i -> i.getValue() == slot.getItem())
                .findFirst().get().getKey();*/
            c.write(getOnMoveLtoSDonePacket(slot, pos))
            moveLockerToStorage(slot, pos)
        }

        private fun getOnMoveLtoSDonePacket(slot: ItemSlotLocker, pos: Short): Packet {
            val pw = PacketWriter(58)

            pw.writeHeader(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT)
            pw.write(CashItemResult.MOVE_LTOS_DONE.value)
            pw.writeShort(pos)
            slot.item.encode(pw)

            return pw.createPacket()
        }

        private fun sendSetWish(reader: PacketReader, c: Client) {
            c.character.wishlist = IntArray(10)
            for (i in 0..9) {
                val sn = reader.readInteger()
                if (sn == 0) {
                    continue
                }

                val commodity = getCommodity(sn)
                if (commodity == null) {
                    System.err.println("[SET_WISH] commodity is null")
                    continue
                }

                if (!commodity.isOnSale) {
                    System.err.println("[SET_WISH] commodity is not on sale")
                    continue
                }

                c.character.wishlist[i] = sn
            }
            c.updateWishlist()
        }

        private fun sendOnBuyFriendship(reader: PacketReader, c: Client) {
            val spw = reader.readMapleString()
            if (spw != c.pic) {
                failRequest(c, 34)
                return
            }

            val cashType = reader.readInteger()
            if (cashType != 4) {
                System.err.println("[BUY] invalid cash type " + cashType + " - " + c.character.name)
                failRequest(c, 2)
                return  // we don't support anything but "NX Prepaid"
            }

            val sn = reader.readInteger()
            val giveTo = reader.readMapleString()
            val text = reader.readMapleString()
            val cid = getOfflineId(giveTo)

            if (!giftChecks(c, spw, text, cid)) return

            val chr = getCharacter(cid)
            val aid = getOfflineAccountId(c, chr, cid)
            if (aid == -1) return

            val commodity = getCommodity(sn)
            if (!commodityBuyChecks(c, commodity, sn, chr, aid)) return

            println("spw: $spw")
            println("nCommSN: $sn")
            println("giveTo: $giveTo")
            println("text: $text")
        }

        /**
         * @param c      Client to send to
         * @param reason 1: Request timed out.\r\nPlease try again.
         * 3: You don't have enough cash.
         * 4: You can't buy someone a cash item gift if you're under 14.
         * 5: You have exceeded the allotted limit of price\r\nfor gifts.
         * 6: You cannot send a gift to your own account.\r\nPlease purchase it after logging\r\nin with the related character.
         * 7: Please confirm whether\r\nthe character's name is correct.
         * 8: This item has a gender restriction.\r\nPlease confirm the gender of the recipient.
         * 9: The gift cannot be sent because\r\nthe recipient's Inventory is full.
         * 10: Please check and see if you have exceeded\r\nthe number of cash items you can have.
         * 11: Please check and see\r\nif the name of the character is wrong,\r\nor if the item has gender restrictions.
         * 14: Please check and see if \r\nthe coupon number is right.
         * 16: This coupon has expired.
         * 17: This coupon was already used.
         * 18: This coupon can only be used at\r\nNexon-affiliated Internet Cafe's.\r\nPlease use the Nexon-affiliated Internet Cafe's.
         * 19: This coupon is a Nexon-affiliated Internet Cafe-only coupon,\r\nand it had already been used.
         * 20: This coupon is a Nexon-affiliated Internet Cafe-only coupon,\r\nand it had already been expired.
         * 21: This is the NX coupon number.\r\nRegister your coupon at www.nexon.net.
         * 22: Due to gender restrictions, the coupon \r\nis unavailable for use.
         * 23: This coupon is only for regular items, and \r\nit's unavailable to give away as a gift.
         * 24: This coupon is only for MapleStory, and\r\nit cannot be gifted to others.
         * 25: Please check if your inventory is full or not.
         * 26: This item is only available for purchase by a user at the premium service internet cafe.
         * 27: You are sending a gift to an invalid recipient.\r\nPlease check the character name and gender.
         * 28: Please check the name of the receiver.
         * 29: Items are not available for purchase\r\n at this hour.
         * 30: The item is out of stock, and therefore\r\nnot available for sale.
         * 31: You have exceeded the spending limit of NX.
         * 32: You do not have enough mesos.
         * 33: The Cash Shop is unavailable\r\nduring the beta-test phase.\r\nWe apologize for your inconvenience.
         * 34: Check your PIC password and\r\nplease try again.
         * 37: This coupon is only available to the users buying cash item for the first time.
         * 38: You have already applied for this.
         * 43: You have reached the daily maximum \r\npurchase limit for the Cash Shop.
         * 46: You have exceeded the maximum number\r\nof usage per account\for this account.\r\nPlease check the coupon for detail.
         * 48: The coupon system will be available soon.
         * 49: This item can only be used 15 days \r\nafter the account's registration.
         * 50: You do not have enough Gift Tokens \r\nin your account. Please charge your account \r\nwith Nexon Game Cards to receive \r\nGift Tokens to gift this item.
         * 51: Due to technical difficulties,\r\nthis item cannot be sent at this time.\r\nPlease try again.
         * 52: You may not gift items for \r\nit has been less than two weeks \r\nsince you first charged your account.
         * 53: Users with history of illegal activities\r\n may not gift items to others. Please make sure \r\nyour account is neither previously blocked, \r\nnor illegally charged with NX.
         * 54: Due to limitations, \r\nyou may not gift this item as this time. \r\nPlease try again later.
         * 55: You have exceeded the amount of time \r\nyou can gift items to other characters.
         * 56: This item cannot be gifted \r\ndue to technical difficulties. \r\nPlease try again later.
         * 57: You cannot transfer \r\na character under level 20.
         * 58: You cannot transfer a character \r\nto the same world it is currently in.
         * 59: You cannot transfer a character \r\ninto the new server world.
         * 60: You may not transfer out of this \r\nworld at this time.
         * 61: You cannot transfer a character into \r\na world that has no empty character slots.
         * 62: The event has either ended or\r\nthis item is not available for free testing.
         * 63: This item cannot be purchased \r\nwith MaplePoints.
         * 64: Sorry for inconvinence. \r\nplease try again.
         * 65: You can no longer purchase or gift that Item of the Day.
         * 66: This item cannot be\r\npurchased by anyone under 7.
         * 67: This item cannot be\r\nreceived by anyone under 7.
         * 68: You have reached the daily maximum \r\npurchase limit for the Cash Shop.
         * 69: NX use is restricted.\r\nPlease change your settings in the NX Security Settings menu\r\nin the Nexon Portal My Info section.
         * default: Due to an unknown error,\r\nthe request for Cash Shop has failed.
         */
        private fun failRequest(c: Client, reason: Int) {
            val pw = PacketWriter(3)

            pw.writeHeader(SendOpcode.CASH_SHOP_CASH_ITEM_RESULT)
            pw.write(CashItemResult.BUY_FAILED.value)
            pw.write(reason)

            c.write(pw.createPacket())
        }
    }
}