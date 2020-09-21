package net.maple.handlers.user

import client.Client
import net.maple.handlers.PacketHandler
import util.packet.PacketReader

class UserMigrateToITCRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        c.character.enableActions()
    }
}