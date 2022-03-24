package scripting.scripts.portal

import client.Client
import client.messages.broadcast.types.AlertMessage
import field.obj.portal.FieldPortal
import managers.NPCManager
import net.maple.handlers.user.UserSelectNpcHandler
import net.maple.packets.CharacterPackets.message
import scripting.portal.Portal
import scripting.portal.PortalScript

@Portal(["EnterBoss"])
class EnterBoss : PortalScript() {

    private companion object {
        val npcByMap = mapOf(
            1998 to 9000027,
            2998 to 2120004,
            10998 to 2071011,
            13998 to 9120010,
            15998 to 1061013
        )
    }

    override fun onEnter(c: Client, portal: FieldPortal) {
        npcByMap[c.character.fieldId]?.let {
            UserSelectNpcHandler.openNpc(c, NPCManager.getNPC(it))
        } ?: run {
            c.character.message(AlertMessage(("Something went wrong.")))
        }
    }
}