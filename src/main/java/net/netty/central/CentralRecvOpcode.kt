package net.netty.central

enum class CentralRecvOpcode(val value: Int) {

    ON_CONNECT(0x01),
    MIGRATE_INFO(0x02);
}