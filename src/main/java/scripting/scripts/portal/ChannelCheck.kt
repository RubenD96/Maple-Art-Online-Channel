package scripting.scripts.portal

import client.Client
import client.messages.broadcast.types.AlertMessage
import field.obj.portal.FieldPortal
import net.maple.packets.CharacterPackets.message
import scripting.portal.Portal
import scripting.portal.PortalScript

@Portal(["Channel"])
class ChannelCheck : PortalScript() {

    override fun onEnter(c: Client, portal: FieldPortal) {
        if (c.worldChannel.channelId + 1 == 1) {
            portal.forceEnter(c.character)
        } else {
            c.character.message(AlertMessage(("You can only enter this map in channel 1!")))
        }
    }
}