package net.maple.handlers.user

import client.Client
import client.messages.broadcast.types.AlertMessage
import constants.FieldConstants.isTownMap
import managers.NPCManager
import net.maple.handlers.PacketHandler
import net.maple.packets.CharacterPackets.message
import scripting.ScriptManager
import scripting.npc.NPCScriptManager
import util.packet.PacketReader

class UserMigrateToITCRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        if (c.character.field.template.isTownMap()) {
            UserSelectNpcHandler.openNpc(c, NPCManager.getNPC(1032102))
        } else {
            c.character.message(AlertMessage("You may only access portals in maps with a town portal in it"))
        }
        c.character.enableActions()
    }
}