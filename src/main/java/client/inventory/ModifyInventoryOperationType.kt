package client.inventory

enum class ModifyInventoryOperationType(val type: Int) {
    ADD(0x00),
    UPDATE_QUANTITY(0x01),
    MOVE(0x02),
    REMOVE(0x03),
    UPDATE_EXP(0x04);
}