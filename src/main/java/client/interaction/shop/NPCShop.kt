package client.interaction.shop

import client.Character
import client.Client
import client.interaction.Interactable
import client.inventory.ItemInventoryType
import client.inventory.item.templates.ItemBundleTemplate
import client.inventory.item.slots.ItemSlotBundle
import constants.ItemConstants
import database.jooq.Tables.SHOPITEMS
import managers.ItemManager
import net.database.ShopAPI.getShopsItems
import net.maple.SendOpcode
import net.maple.packets.CharacterPackets.modifyInventory
import util.packet.PacketWriter
import kotlin.math.max

class NPCShop(val id: Int) : Interactable {

    val items: MutableMap<Int, NPCShopItem> = LinkedHashMap()

    override fun open(chr: Character) {
        if (chr.npcShop != null) {
            chr.client.close(this, "Attempting to open a shop while in a shop")
            return
        }
        val pw = PacketWriter(32)

        pw.writeHeader(SendOpcode.OPEN_SHOP_DLG)
        pw.writeInt(id)

        pw.writeShort(items.size)
        items.values.forEach {
            pw.writeInt(it.id)
            pw.writeInt(it.price)
            pw.write(it.discountRate.toInt())
            pw.writeInt(it.tokenId)
            pw.writeInt(it.tokenPrice)
            pw.writeInt(it.itemPeriod)
            pw.writeInt(it.levelLimited)

            if (!ItemConstants.isRechargeableItem(it.id)) {
                pw.writeShort(it.quantity)
            } else {
                pw.writeDouble(it.unitPrice)
            }

            pw.writeShort(it.maxPerSlot)
        }

        chr.write(pw.createPacket())
        chr.npcShop = this
    }

    override fun close(c: Client) {
        c.character.npcShop = null
    }

    fun buy(chr: Character, pos: Short, itemId: Int, quantity: Short): ShopResult {
        if (pos >= items.size) return ShopResult.CANT_BUY_ANYMORE

        val shopItem = items[itemId] ?: return ShopResult.BUY_UNKNOWN
        val item = ItemManager.getItem(shopItem.id)

        var count = quantity
        if (shopItem.quantity > 1) count = 1
        count = max(count.toInt(), shopItem.maxPerSlot.toInt()).toShort()
        count = max(count.toInt(), 1).toShort()

        if (shopItem.price > 0 && chr.meso < shopItem.price * count) return ShopResult.BUY_NO_MONEY
        if (shopItem.tokenId > 0 && chr.getItemQuantity(shopItem.tokenId) < shopItem.tokenPrice * count) return ShopResult.BUY_NO_TOKEN
        if (chr.level < shopItem.levelLimited) return ShopResult.LIMIT_LEVEL_LESS

        val slot = item.toItemSlot()
        if (slot is ItemSlotBundle) {
            if (ItemConstants.isRechargeableItem(slot.templateId)) {
                slot.number = slot.maxNumber
            } else {
                slot.number = (count * shopItem.quantity).toShort()
            }
        }

        if (!chr.hasInvSpace(slot)) return ShopResult.BUY_UNKNOWN

        if (shopItem.itemPeriod > 0) slot.expire = 0 // todo

        if (shopItem.price > 0) {
            chr.gainMeso(-(shopItem.price * count))
        } else if (shopItem.tokenId > 0) {
            chr.modifyInventory({ it.remove(id, (-(shopItem.tokenPrice * count)).toShort()) })
        }

        chr.modifyInventory({ it.add(item, count) })
        return ShopResult.BUY_SUCCESS
    }

    fun sell(chr: Character, pos: Short, itemId: Int, count: Short): ShopResult {
        val type = ItemInventoryType.values()[itemId / 1000000 - 1]
        val inventory = chr.getInventory(type)

        if (!inventory.items.containsKey(pos)) return ShopResult.SELL_UNKNOWN // redundant statement?

        val slot = inventory.items[pos] ?: return ShopResult.SELL_UNKNOWN
        val item = ItemManager.getItem(slot.templateId)
        var price = item.sellPrice

        if (ItemConstants.isRechargeableItem(item.id)) {
            price += (slot as ItemSlotBundle).number * (item as ItemBundleTemplate).unitPrice.toInt()
        } else {
            price *= count
        }

        if (chr.meso.toLong() + price.toLong() > Int.MAX_VALUE) return ShopResult.SELL_UNKNOWN

        chr.modifyInventory({ it.remove(slot, count) })
        chr.gainMeso(price)
        return ShopResult.SELL_SUCCESS
    }

    enum class ShopResult(val value: Int) {
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
    }

    init {
        val data = getShopsItems(id)
        data.forEach {
            val item = NPCShopItem(it.getValue(SHOPITEMS.ITEM))
            item.price = it.getValue(SHOPITEMS.PRICE)
            item.tokenId = it.getValue(SHOPITEMS.TOKEN_ID)
            item.tokenPrice = it.getValue(SHOPITEMS.TOKEN_PRICE)
            item.itemPeriod = it.getValue(SHOPITEMS.PERIOD)
            item.levelLimited = it.getValue(SHOPITEMS.LEVEL_LIMIT)
            item.stock = it.getValue(SHOPITEMS.STOCK)
            item.unitPrice = it.getValue(SHOPITEMS.UNIT_PRICE)
            item.maxPerSlot = it.getValue(SHOPITEMS.MAX_SLOT)
            item.quantity = it.getValue(SHOPITEMS.QUANTITY)
            item.discountRate = it.getValue(SHOPITEMS.DISCOUNT)
            items[item.id] = item
        }
    }
}