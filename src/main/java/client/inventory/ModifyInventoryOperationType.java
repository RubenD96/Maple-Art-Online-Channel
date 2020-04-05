package client.inventory;

public enum ModifyInventoryOperationType {

    ADD(0x00),
    UPDATE_QUANTITY(0x01),
    MOVE(0x02),
    REMOVE(0x03),
    UPDATE_EXP(0x04);

    private final int type;

    ModifyInventoryOperationType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
