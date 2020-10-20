package scripting.portal

import client.Client
import client.messages.broadcast.types.AlertMessage
import field.obj.portal.FieldPortal
import net.maple.packets.CharacterPackets.message
import scripting.AbstractScriptManager

object PortalScriptManager : AbstractScriptManager() {

    fun execute(c: Client, portal: FieldPortal, script: String) {
        try {
            val portalScriptMethods = PortalScriptMethods(c, portal)
            val iv = getInvocable("portal/$script.js", c)

            if (iv == null) {
                c.character.message(AlertMessage("Portal script $script does not exist"))
                println("Portal " + script + " is uncoded. (" + portal.id + ")")
                return
            }

            engine?.run {
                this.put("portal", portalScriptMethods)
                iv.invokeFunction("execute")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}