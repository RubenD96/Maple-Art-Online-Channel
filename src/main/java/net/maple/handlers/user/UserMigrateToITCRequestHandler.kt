package net.maple.handlers.user

import client.Client
import net.maple.handlers.PacketHandler
import scripting.dialog.npc.NPCScriptManager
import util.packet.PacketReader

class UserMigrateToITCRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        //NPCScriptManager.converse(c, 1032102)
        NPCScriptManager[1032102]?.start(c)
        c.character.enableActions()
    }
}