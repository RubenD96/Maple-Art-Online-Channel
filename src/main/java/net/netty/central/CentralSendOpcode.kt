package net.netty.central

import util.packet.IntegerValue

enum class CentralSendOpcode(override val value: Int) : IntegerValue {

    CHANNEL_INFO(0x01),
    ADD_ONLINE_PLAYER(0x02),
    REMOVE_ONLINE_PLAYER(0x03);
}