package net.maple.handlers.user

import client.Client
import net.maple.handlers.PacketHandler
import util.logging.LogType
import util.logging.Logger
import util.packet.PacketReader
import java.awt.Rectangle

class UserPortalTeleportRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        val fieldKey = reader.readByte()
        if (fieldKey != chr.fieldKey) {
            return run {
                Logger.log(LogType.FIELD_KEY, "FieldKey mismatch", this, c)
                c.close(this, "FieldKey mismatch")
            }
        }

        val portalName = reader.readMapleString()
        val portal = chr.field.getPortalByName(portalName) ?: return run {
            Logger.log(LogType.NULL, "Trying to use a non-existing portal $portalName", this, c)
            c.close()
        }

        val from = reader.readPoint()
        val to = reader.readPoint()

        if (!portal.rect.contains(from)) {
            return run {
                Logger.log(LogType.INVALID, "Too far away from the origin portal: ${portal.position} | $from", this, c)
                c.close()
            }
        }

        val targetPortal = chr.field.getPortalByName(portal.targetName) ?: return run {
            Logger.log(LogType.NULL, "Trying to warp to a non-existing portal ${portal.targetName} (from: $portalName)", this, c)
            c.close()
        }
        if (!targetPortal.rect.contains(to)) {
            return run {
                Logger.log(LogType.INVALID, "Too far away from the origin portal: ${targetPortal.position} | $to", this, c)
                c.close()
            }
        }
    }
}