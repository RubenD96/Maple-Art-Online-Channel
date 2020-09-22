package client.inventory

enum class ItemInventoryType(val type: Int) {
    EQUIP(0x01),
    CONSUME(0x02),
    INSTALL(0x03),
    ETC(0x04),
    CASH(0x05);
}