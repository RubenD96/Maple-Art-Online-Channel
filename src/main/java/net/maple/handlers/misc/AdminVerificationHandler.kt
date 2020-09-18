package net.maple.handlers.misc

import client.Client
import net.maple.handlers.PacketHandler
import util.packet.PacketReader

class AdminVerificationHandler : PacketHandler() {

    override fun handlePacket(reader: PacketReader, c: Client) {
        if (!c.isAdmin) {
            c.close(this, "Admin packet from non-admin user (${c.character.getName()})")
        }
    }
}