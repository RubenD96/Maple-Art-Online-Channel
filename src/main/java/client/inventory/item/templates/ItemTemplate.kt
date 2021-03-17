package client.inventory.item.templates

import client.inventory.item.flags.ItemFlag
import client.inventory.item.slots.ItemSlot
import client.inventory.item.variation.ItemVariationType
import util.packet.PacketReader

open class ItemTemplate(val id: Int) {

    private var flags: Int = 0
    var sellPrice = 0
    var isTimeLimited = false
    var isQuest = false
    var isPartyQuest = false
    var isOnly = false
    var isTradeBlock = false
    var isNotSale = false
    var isBigSize = false
    var isExpireOnLogout = false
    var isAccountSharable = false
    var isCash = false

    open fun decode(r: PacketReader): ItemTemplate {
        flags = r.readInteger()
        if (containsFlag(ItemFlag.PRICE)) sellPrice = r.readInteger()
        if (containsFlag(ItemFlag.TIME_LIMITED)) isTimeLimited = r.readBool()
        if (containsFlag(ItemFlag.QUEST)) isQuest = r.readBool()
        if (containsFlag(ItemFlag.PARTY_QUEST)) isPartyQuest = r.readBool()
        if (containsFlag(ItemFlag.ONLY)) isOnly = r.readBool()
        if (containsFlag(ItemFlag.TRADE_BLOCK)) isTradeBlock = r.readBool()
        if (containsFlag(ItemFlag.NOT_SALE)) isNotSale = r.readBool()
        if (containsFlag(ItemFlag.BIG_SIZE)) isBigSize = r.readBool()
        if (containsFlag(ItemFlag.EXPIRE_ON_LOGOUT)) isExpireOnLogout = r.readBool()
        if (containsFlag(ItemFlag.ACCOUNT_SHARE)) isAccountSharable = r.readBool()
        if (containsFlag(ItemFlag.CASH)) isCash = r.readBool()

        return this
    }

    fun containsFlag(flag: ItemFlag): Boolean {
        return flags and flag.value == flag.value
    }

    open fun toItemSlot(): ItemSlot {
        return toItemSlot(ItemVariationType.BASE)
    }

    open fun toItemSlot(type: ItemVariationType): ItemSlot {
        return when (this) {
            is ItemEquipTemplate -> this.toItemSlot(type)
            is ItemBundleTemplate -> this.toItemSlot()
            is PetItemTemplate -> this.toItemSlot()
            else -> throw IllegalArgumentException()
        }
    }

    override fun toString(): String {
        return "ItemTemplate(id=$id, flags=$flags, sellPrice=$sellPrice, isTimeLimited=$isTimeLimited, isQuest=$isQuest, isPartyQuest=$isPartyQuest, isOnly=$isOnly, isTradeBlock=$isTradeBlock, isNotSale=$isNotSale, isBigSize=$isBigSize, isExpireOnLogout=$isExpireOnLogout, isAccountSharable=$isAccountSharable, isCash=$isCash)"
    }
}