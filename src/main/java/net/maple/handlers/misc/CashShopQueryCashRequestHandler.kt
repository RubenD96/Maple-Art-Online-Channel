package net.maple.handlers.misc

import client.Client
import net.maple.handlers.PacketHandler
import net.maple.packets.CashShopPackets
import util.packet.PacketReader

class CashShopQueryCashRequestHandler : PacketHandler() {

    override fun handlePacket(reader: PacketReader, c: Client) {
        CashShopPackets.sendCashData(c)
    }
}