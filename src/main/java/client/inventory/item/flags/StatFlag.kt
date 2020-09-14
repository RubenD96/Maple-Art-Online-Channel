package client.inventory.item.flags

import util.packet.IntegerValue

enum class StatFlag(private val flag: Int) : IntegerValue {

    HP(0x01),
    MP(0x02),
    HPR(0x04),
    MPR(0x08),
    NO_CANCEL_MOUSE(0x10),
    PAD(0x20),
    PDD(0x40),
    MAD(0x80),
    MDD(0X100),
    ACC(0x200),
    EVA(0x400),
    CRAFT(0x800),
    SPEED(0x1000),
    JUMP(0x2000),
    MORPH(0x4000),
    TIME(0x8000);

    override fun getValue(): Int {
        return flag
    }

    override fun setValue(value: Int) {
        //...
    }
}