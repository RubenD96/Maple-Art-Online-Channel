package client.inventory.item.flags;

import util.packet.IntegerValue;

public enum  ItemFlag implements IntegerValue {

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

    private final int flag;

    ItemFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public int getValue() {
        return flag;
    }

    @Override
    public void setValue(int value) {
        //...
    }
}
