package client.pet

enum class PetActType(val value: Byte) {

    INTERACT(0x00),
    FEED(0x01),
    CHAT(0x02),
    RANDOM(0x03);
}