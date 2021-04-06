package net.netty.central

import util.packet.IntegerValue

enum class CentralSendOpcode(override val value: Int) : IntegerValue {

    CHANNEL_INFO(0x01),
    ADD_ONLINE_PLAYER(0x02),
    REMOVE_ONLINE_PLAYER(0x03),
    PING(0x04);

    companion object {
        fun getStringByCode(code: Int): String {
            for (op in values()) {
                if (op.value == code) return op.name
            }
            return "UNKNOWN_OPCODE"
        }
    }
}