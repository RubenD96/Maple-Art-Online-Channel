package net.maple.handlers.user

import client.Client
import net.maple.handlers.PacketHandler
import util.packet.PacketReader

class UserPortalScriptRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        reader.readByte()
        val name = reader.readMapleString()

        val portal = c.character.field.getPortalByName(name) ?: return

        if (portal.script.isNotEmpty()) {
            println(portal)
            c.character.enableActions()
        }
    }
}