package net.maple.handlers.user

import client.Client
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import net.maple.packets.CashShopPackets
import net.server.Server.clients
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter

class UserMigrateToCashShopRequestHandler : PacketHandler() {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        c.acquireMigrateState()
        try {
            if (!chr.isInCashShop) {
                chr.save()
                chr.isInCashShop = true
                clients[c.accId]!!.cashShop = true
                CashShopPackets.sendSetCashShop(c)
                chr.field.leave(chr)
                //chr.field = null
            } else {
                c.write(fail())
            }
        } finally {
            c.releaseMigrateState()
        }
    }

    companion object {
        private fun fail(): Packet {
            val pw = PacketWriter(3)

            pw.writeHeader(SendOpcode.TRANSFER_CHANNEL_REQ_IGNORED)
            pw.write(0x02)

            return pw.createPacket()
        }
    }
}