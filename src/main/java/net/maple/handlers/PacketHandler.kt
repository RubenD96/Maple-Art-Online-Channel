package net.maple.handlers

import client.Client
import util.packet.PacketReader

interface PacketHandler {

    fun handlePacket(reader: PacketReader, c: Client)

    fun validateState(c: Client): Boolean {
        return c.isLoggedIn
    }
}