package client.inventory.item.flags

import util.packet.IntegerValue

enum class ItemFlag(private val flag: Int) : IntegerValue {

    PRICE(0x01),
    TIME_LIMITED(0x02),
    QUEST(0x04),
    PARTY_QUEST(0x08),
    ONLY(0x10),
    TRADE_BLOCK(0x20),
    NOT_SALE(0x40),
    BIG_SIZE(0x80),
    EXPIRE_ON_LOGOUT(0X100),
    ACCOUNT_SHARE(0x200),
    CASH(0x400);

    override fun getValue(): Int {
        return flag
    }

    override fun setValue(value: Int) {
        //...
    }
}