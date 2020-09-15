package client.inventory.item.flags

import util.packet.IntegerValue

enum class EquipFlag(override val value: Int) : IntegerValue {

    TUC(0x01),
    INC_STR(0x02),
    INC_DEX(0x04),
    INC_INT(0x08),
    INC_LUK(0x10),
    INC_MAX_HP(0x20),
    INC_MAX_MP(0x40),
    INC_MAX_HPR(0x80),
    INC_MAX_MPR(0x100),
    INC_PAD(0x200),
    INC_MAD(0x400),
    INC_PDD(0x800),
    INC_MDD(0x1000),
    INC_ACC(0x2000),
    INC_EVA(0x4000),
    INC_CRAFT(0x8000),
    INC_SPEED(0x10000),
    INC_JUMP(0x20000),
    ONLY_EQUIP(0x40000),
    TRADE_BLOCK_EQUIP(0x80000),
    NOT_EXTEND(0x100000),
    SHARABLE_ONCE(0x200000),
    APPLIABLE_KARMA_TYPE(0x400000),
    SET_ITEM_ID(0x800000),
    DURABILITY(0x1000000);
}