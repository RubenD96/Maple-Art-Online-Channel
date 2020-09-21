package net.maple.handlers.net

import client.Client
import net.maple.handlers.PacketHandler
import util.packet.PacketReader

class PongHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        c.lastPong = System.currentTimeMillis()
    }

    override fun validateState(c: Client): Boolean {
        return true
    }
}