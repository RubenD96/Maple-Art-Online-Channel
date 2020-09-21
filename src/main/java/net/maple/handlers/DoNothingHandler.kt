package net.maple.handlers

import client.Client
import util.packet.PacketReader

class DoNothingHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        // ...
    }

    override fun validateState(c: Client): Boolean {
        return true
    }
}