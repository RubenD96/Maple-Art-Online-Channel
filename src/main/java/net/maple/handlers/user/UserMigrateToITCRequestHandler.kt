package net.maple.handlers.user

import client.Client
import net.maple.handlers.PacketHandler
import scripting.ScriptManager
import scripting.npc.NPCScriptManager
import util.packet.PacketReader

class UserMigrateToITCRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        NPCScriptManager.converse(c, 1032102)
        ScriptManager.npcScripts[1032102]?.start()
        c.character.enableActions()
    }
}