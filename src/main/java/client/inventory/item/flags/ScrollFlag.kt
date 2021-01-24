package client.inventory.item.flags

import util.packet.IntegerValue

enum class ScrollFlag(override val value: Int) : IntegerValue {

    INC_MHP(0x01),
    INC_MMP(0x02),
    INC_STR(0x04),
    INC_DEX(0x08),
    INC_INT(0x10),
    INC_LUK(0x20),
    INC_PAD(0x40),
    INC_PDD(0x80),
    INC_MAD(0x100),
    INC_MDD(0X200),
    INC_ACC(0x400),
    INC_EVA(0x800),
    INC_SPEED(0x1000),
    INC_JUMP(0x2000),
    INC_CRAFT(0x4000),
    ENCHANT_CATEGORY(0x8000),
    SUCCESS(0x10000),
    CURSED(0x20000);
}