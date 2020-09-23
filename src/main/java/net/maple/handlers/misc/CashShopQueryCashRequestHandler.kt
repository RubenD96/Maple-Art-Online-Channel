package net.maple.handlers.misc

import client.Client
import net.maple.handlers.PacketHandler
import net.maple.packets.CashShopPackets.sendCashData
import util.packet.PacketReader

class CashShopQueryCashRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        c.sendCashData()
    }
}