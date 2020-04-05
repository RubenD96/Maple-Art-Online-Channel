package client.inventory;

public enum ItemInventoryType {

    EQUIP(0x01),
    CONSUME(0x02),
    INSTALL(0x03),
    ETC(0x04),
    CASH(0x05);

    private final int type;

    ItemInventoryType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
