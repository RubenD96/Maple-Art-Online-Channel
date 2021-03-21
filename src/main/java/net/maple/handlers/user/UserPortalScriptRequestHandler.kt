package net.maple.handlers.user

import client.Client
import net.maple.handlers.PacketHandler
import util.logging.LogType
import util.logging.Logger
import util.packet.PacketReader

class UserPortalScriptRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character
        val fieldKey = reader.readByte()
        if (fieldKey != chr.fieldKey) {
            return run {
                Logger.log(LogType.FIELD_KEY, "FieldKey mismatch", this, c)
                c.close(this, "FieldKey mismatch")
            }
        }

        val name = reader.readMapleString()

        val portal = chr.field.getPortalByName(name) ?: return

        if (portal.script.isNotEmpty()) {
            println(portal)
            chr.enableActions()
        }
    }
}