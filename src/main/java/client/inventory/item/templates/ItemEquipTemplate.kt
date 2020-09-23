package client.inventory.item.templates

import client.inventory.item.flags.EquipFlag
import client.inventory.item.slots.ItemSlotEquip
import client.inventory.item.variation.ItemVariation
import client.inventory.item.variation.ItemVariationType
import util.packet.PacketReader

class ItemEquipTemplate(id: Int, r: PacketReader) : ItemTemplate(id, r) {

    val equipFlags: Int
    val reqSTR: Short
    val reqDEX: Short
    val reqINT: Short
    val reqLUK: Short
    val reqFAME: Short
    val reqJob: Short
    val reqLevel: Byte
    var tUC: Byte = 0
    var incSTR: Short = 0
    var incDEX: Short = 0
    var incINT: Short = 0
    var incLUK: Short = 0
    var incMaxHP = 0
    var incMaxMP = 0
    var incMaxHPr = 0
    var incMaxMPr = 0
    var incPAD: Short = 0
    var incMAD: Short = 0
    var incPDD: Short = 0
    var incMDD: Short = 0
    var incACC: Short = 0
    var incEVA: Short = 0
    var incCraft: Short = 0
    var incSpeed: Short = 0
    var incJump: Short = 0
    var isOnlyEquip = false
    var isTradeBlockEquip = false
    var isNotExtend = false
    var isSharableOnce = false
    var appliableKarmaType: Byte = 0
    var setItemId = 0
    var durability = -1

    init {
        equipFlags = r.readInteger()
        reqSTR = r.readShort()
        reqDEX = r.readShort()
        reqINT = r.readShort()
        reqLUK = r.readShort()
        reqFAME = r.readShort()
        reqJob = r.readShort()
        reqLevel = r.readByte()
        if (containsFlag(EquipFlag.TUC)) tUC = r.readByte()
        if (containsFlag(EquipFlag.INC_STR)) incSTR = r.readShort()
        if (containsFlag(EquipFlag.INC_DEX)) incDEX = r.readShort()
        if (containsFlag(EquipFlag.INC_INT)) incINT = r.readShort()
        if (containsFlag(EquipFlag.INC_LUK)) incLUK = r.readShort()
        if (containsFlag(EquipFlag.INC_MAX_HP)) incMaxHP = r.readInteger()
        if (containsFlag(EquipFlag.INC_MAX_MP)) incMaxMP = r.readInteger()
        if (containsFlag(EquipFlag.INC_MAX_HPR)) incMaxHPr = r.readInteger()
        if (containsFlag(EquipFlag.INC_MAX_MPR)) incMaxMPr = r.readInteger()
        if (containsFlag(EquipFlag.INC_PAD)) incPAD = r.readShort()
        if (containsFlag(EquipFlag.INC_MAD)) incMAD = r.readShort()
        if (containsFlag(EquipFlag.INC_PDD)) incPDD = r.readShort()
        if (containsFlag(EquipFlag.INC_MDD)) incMDD = r.readShort()
        if (containsFlag(EquipFlag.INC_ACC)) incACC = r.readShort()
        if (containsFlag(EquipFlag.INC_EVA)) incEVA = r.readShort()
        if (containsFlag(EquipFlag.INC_CRAFT)) incCraft = r.readShort()
        if (containsFlag(EquipFlag.INC_SPEED)) incSpeed = r.readShort()
        if (containsFlag(EquipFlag.INC_JUMP)) incJump = r.readShort()
        if (containsFlag(EquipFlag.ONLY_EQUIP)) isOnlyEquip = r.readBool()
        if (containsFlag(EquipFlag.TRADE_BLOCK_EQUIP)) isTradeBlockEquip = r.readBool()
        if (containsFlag(EquipFlag.NOT_EXTEND)) isNotExtend = r.readBool()
        if (containsFlag(EquipFlag.SHARABLE_ONCE)) isSharableOnce = r.readBool()
        if (containsFlag(EquipFlag.APPLIABLE_KARMA_TYPE)) appliableKarmaType = r.readByte()
        if (containsFlag(EquipFlag.SET_ITEM_ID)) setItemId = r.readInteger()
        if (containsFlag(EquipFlag.DURABILITY)) durability = r.readInteger()
    }

    fun containsFlag(flag: EquipFlag): Boolean {
        return equipFlags and flag.value == flag.value
    }

    fun fromDbToSlot(
            TUC: Byte,
            str: Short, dex: Short, int_: Short, luk: Short, hp: Short, mp: Short,
            pad: Short, mad: Short, pdd: Short, mdd: Short, acc: Short, eva: Short,
            speed: Short, jump: Short, craft: Short, durability: Int
    ): ItemSlotEquip {
        val equip = ItemSlotEquip()
        equip.templateId = id
        equip.ruc = TUC
        equip.str = str
        equip.dex = dex
        equip.int = int_
        equip.luk = luk
        equip.maxHP = hp
        equip.maxMP = mp
        equip.pad = pad
        equip.mad = mad
        equip.pdd = pdd
        equip.mdd = mdd
        equip.acc = acc
        equip.eva = eva
        equip.speed = speed
        equip.jump = jump
        equip.craft = craft
        equip.durability = durability
        return equip
    }

    override fun toItemSlot(type: ItemVariationType): ItemSlotEquip {
        val variation = ItemVariation(type)
        val equip = ItemSlotEquip()
        equip.templateId = id
        equip.ruc = tUC
        equip.str = variation[incSTR.toInt()].toShort()
        equip.dex = variation[incDEX.toInt()].toShort()
        equip.int = variation[incINT.toInt()].toShort()
        equip.luk = variation[incLUK.toInt()].toShort()
        equip.maxHP = variation[incMaxHP].toShort()
        equip.maxMP = variation[incMaxMP].toShort()
        equip.pad = variation[incPAD.toInt()].toShort()
        equip.mad = variation[incMAD.toInt()].toShort()
        equip.pdd = variation[incPDD.toInt()].toShort()
        equip.mdd = variation[incMDD.toInt()].toShort()
        equip.acc = variation[incACC.toInt()].toShort()
        equip.eva = variation[incEVA.toInt()].toShort()
        equip.craft = variation[incCraft.toInt()].toShort()
        equip.speed = variation[incSpeed.toInt()].toShort()
        equip.jump = variation[incJump.toInt()].toShort()
        equip.durability = 100
        return equip
    }
}