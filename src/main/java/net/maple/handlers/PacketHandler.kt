package net.maple.handlers

import client.Client
import util.packet.PacketReader

abstract class PacketHandler {

    abstract fun handlePacket(reader: PacketReader, c: Client)

    open fun validateState(c: Client): Boolean {
        return c.isLoggedIn
    }
}