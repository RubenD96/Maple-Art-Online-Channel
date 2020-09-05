package cashshop.commodities;

public enum CommodityFlag {

    SN(0x01),
    ITEM_ID(0x02),
    COUNT(0x04),
    PRIORITY(0x08),
    PRICE(0x10),
    BONUS(0x20),
    PERIOD(0x40),
    REQ_POP(0x80),
    REQ_LEV(0x100),
    MAPLE_POINT(0x200),
    MESO(0x400),
    FOR_PREMIUM_USER(0x800),
    GENDER(0x1000),
    ON_SALE(0x2000),
    CLASS(0x4000),
    LIMIT(0x8000),
    PB_CASH(0x10000),
    PB_POINT(0x20000),
    PB_GIFT(0x40000);

    private final int value;

    CommodityFlag(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}