package net.maple.handlers.user

import client.Client
import client.messages.broadcast.types.AlertMessage
import constants.FieldConstants
import constants.FieldConstants.isSafeMap
import net.maple.handlers.PacketHandler
import net.maple.packets.CharacterPackets.message
import util.HexTool
import util.logging.LogType
import util.logging.Logger
import util.packet.PacketReader
import java.awt.Point

class UserTransferFieldRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        println(HexTool.toHex(reader.data))
        val chr = c.character
        val cashShop = reader.available() == 0

        if (cashShop) {
            if (!chr.isInCashShop) {
                c.close(this, "Not in CashShop")
                return
            }

            chr.isInCashShop = false
            c.migrate()
        } else {
            val fieldKey = reader.readByte()
            if (fieldKey != chr.fieldKey) {
                return run {
                    Logger.log(LogType.FIELD_KEY, "FieldKey mismatch", this, c)
                }
            }

            val fieldId = reader.readInteger()
            val portalName = reader.readMapleString()
            if (fieldId != -1) {
                val point = if (portalName.isNotEmpty()) {
                    reader.readPoint()
                } else {
                    Point(0, 0)
                }

                val townPortal = reader.readByte()
                val premium = reader.readBool()

                if (c.isAdmin && chr.isAlive()) {
                    val chase = reader.readBool()
                    if (chase) {
                        chr.chasing = true
                        chr.position = Point(reader.readInteger(), reader.readInteger())
                    }
                    chr.changeField(fieldId)
                } else if (!chr.isAlive()) {
                    chr.health = 50
                    if (chr.hardcore && !chr.field.template.isSafeMap() && !chr.safeDeath) {
                        chr.changeField(FieldConstants.HARDCORE_DEATHMAP)
                    } else {
                        chr.changeField(chr.field.template.returnMap)
                        chr.safeDeath = false
                    }
                } else {
                    Logger.log(LogType.HACK, "Using /m or /c without admin acc", this, c)
                    c.close()
                }
            } else {
                val portal = chr.field.getPortalByName(portalName) ?: return run {
                    chr.enableActions()
                    chr.message(AlertMessage("There is a problem with the portal!\r\nName: $portalName"))
                }

                portal.enter(chr)
            }
        }
    }
}