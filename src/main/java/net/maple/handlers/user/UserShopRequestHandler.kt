package net.maple.handlers.user

import client.Client
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import util.packet.PacketReader
import util.packet.PacketWriter

class UserShopRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character
        val shop = chr.npcShop ?: return
        val request = reader.readByte()

        if (request == ShopRequest.CLOSE) {
            shop.close(c)
            return
        }

        val pw = PacketWriter(16)
        pw.writeHeader(SendOpcode.SHOP_RESULT)

        when (request) {
            ShopRequest.BUY,
            ShopRequest.SELL,
            -> {
                val pos = reader.readShort()
                val itemId = reader.readInteger()
                val count = reader.readShort()

                pw.write((if (request == ShopRequest.BUY) shop.buy(chr, pos, itemId, count)
                else shop.sell(chr, pos, itemId, count)).value)
            }
            ShopRequest.RECHARGE -> {
                shop.close(c)
                return
            }
        }
        c.write(pw.createPacket())
    }

    private object ShopRequest {
        const val BUY: Byte = 0x00
        const val SELL: Byte = 0x01
        const val RECHARGE: Byte = 0x02
        const val CLOSE: Byte = 0x03
    }
}