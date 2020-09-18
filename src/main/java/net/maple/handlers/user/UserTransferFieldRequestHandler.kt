package net.maple.handlers.user

import client.Client
import client.messages.broadcast.types.AlertMessage
import net.maple.handlers.PacketHandler
import net.maple.packets.CharacterPackets
import util.packet.PacketReader

class UserTransferFieldRequestHandler : PacketHandler() {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character
        val cashShop = !reader.readBool()

        if (cashShop) {
            if (!chr.isInCashShop) {
                c.close(this, "Not in CashShop")
                return
            }

            chr.isInCashShop = false
            c.migrate()
        } else {
            val id = reader.readInteger()
            if (id != -1) {
                if (c.isAdmin) {
                    chr.changeField(id)
                } else {
                    c.close(this, "Using /m without admin acc")
                }
            } else {
                val portalName = reader.readMapleString()
                val portal = chr.field.getPortalByName(portalName)

                if (portal == null) {
                    chr.enableActions()
                    chr.write(CharacterPackets.message(AlertMessage("There is a problem with the portal!\r\nName: $portalName")))
                    return
                }

                portal.enter(chr)
            }
        }
    }
}