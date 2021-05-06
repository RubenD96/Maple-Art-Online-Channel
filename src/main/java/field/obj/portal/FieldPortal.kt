package field.obj.portal

import client.Character
import client.messages.broadcast.types.AlertMessage
import field.Field
import net.maple.packets.CharacterPackets.message
import scripting.portal.PortalScriptManager
import util.logging.LogType
import util.logging.Logger.log
import java.awt.Rectangle

class FieldPortal : AbstractFieldPortal(), Portal {

    lateinit var field: Field
    val rect: Rectangle get() = Rectangle(position.x - 30, position.y - 30, 60, 60)

    override fun enter(chr: Character) {
        if (targetMap != 999999999) {
            if (script != "") {
                PortalScriptManager[script]?.onEnter(chr.client, this) ?: run {
                    log(LogType.MISSING, "Missing proper portal script $script", this)
                    chr.message(AlertMessage("Portal script $script does not exist"))
                }
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
        val portal = field.getPortalByName(targetName) ?: return error(chr)
        portal.leave(chr)
    }

    override fun leave(chr: Character) {
        field.enter(chr, id.toByte())
    }

    private fun error(chr: Character) {
        chr.enableActions()
        chr.message(
            AlertMessage(
                "There is a problem with the portal!" +
                        "\r\nID: " + id +
                        "\r\nTargetname: " + targetName
            )
        )
        log(LogType.NULL, "target portal does not exist " + toString(), this, chr.client)
    }
}