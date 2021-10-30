package scripting.scripts.portal

import client.Client
import client.messages.broadcast.types.AlertMessage
import field.obj.portal.FieldPortal
import net.maple.packets.CharacterPackets.message
import net.server.Server.houses
import scripting.portal.Portal
import scripting.portal.PortalScript

@Portal(["EnterHouse"])
class EnterHouse : PortalScript() {

    override fun onEnter(c: Client, portal: FieldPortal) {
        if (c.worldChannel.channelId + 1 == 1) {
            houses[c.character.id]?.firstOrNull { it.id == 100000000 + portal.field.template.id }?.let { house ->
                house.field?.let {
                    c.character.changeField(it, "house00")
                } ?: run {
                    val field = c.worldChannel.fieldManager.getField(house.id + house.stage)
                    house.field = field
                    c.character.changeField(field, "house00")
                }
            } ?: run {
                c.character.writeNpc(9000036, "You do not own this house, please find me nearby to negotiate a price!")
            }
        } else {
            c.character.message(AlertMessage(("You can only enter houses in channel 1!")))
        }
    }
}