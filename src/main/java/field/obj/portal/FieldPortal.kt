package field.obj.portal

import client.Character
import client.messages.broadcast.types.AlertMessage
import field.Field
import net.maple.packets.CharacterPackets
import scripting.portal.PortalScriptManager

class FieldPortal(val field: Field) : AbstractFieldPortal(), Portal {

    override fun enter(chr: Character) {
        if (targetMap != 999999999) {
            if (script != "") {
                PortalScriptManager.execute(chr.client, this, script)
                chr.enableActions()
                return
            }

            enterInternal(chr)
        }
    }

    fun forceEnter(chr: Character) {
        if (targetMap != 999999999) {
            enterInternal(chr)
        }
    }

    private fun enterInternal(chr: Character) {
        val field = chr.getChannel().fieldManager.getField(targetMap)
        val portal: Portal = field.getPortalByName(targetName) ?: return error(chr)
        portal.leave(chr)
    }

    override fun leave(chr: Character) {
        field.enter(chr, id.toByte())
    }

    private fun error(chr: Character) {
        chr.enableActions()
        chr.write(CharacterPackets.message(
                AlertMessage("There is a problem with the portal!" +
                        "\r\nID: " + id +
                        "\r\nTargetname: " + targetName))
        )
        System.err.println(this)
    }
}