package scripting.scripts.portal

import client.Client
import field.obj.portal.FieldPortal
import managers.NPCManager
import net.maple.handlers.user.UserSelectNpcHandler
import scripting.portal.Portal
import scripting.portal.PortalScript

@Portal(["TownPortal"])
class TownPortal : PortalScript() {

    override fun start(c: Client, portal: FieldPortal) {
        UserSelectNpcHandler.openNpc(c, NPCManager.getNPC(1032102))
    }
}