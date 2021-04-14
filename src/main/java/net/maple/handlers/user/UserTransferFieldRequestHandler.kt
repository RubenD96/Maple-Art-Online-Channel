package net.maple.handlers.user

import client.Client
import client.messages.broadcast.types.AlertMessage
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
            if (fieldId != -1) {
                if (c.isAdmin) {
                    chr.changeField(fieldId)
                } else {
                    c.close(this, "Using /m without admin acc")
                }
            } else {
                val portalName = reader.readMapleString()
                val portal = chr.field.getPortalByName(portalName) ?: return run {
                    chr.enableActions()
                    chr.message(AlertMessage("There is a problem with the portal!\r\nName: $portalName"))
                }

                val point = if (portalName.isNotEmpty()) {
                    reader.readPoint()
                } else {
                    Point(0, 0)
                }
                println("point: $point")
                val townPortal = reader.readByte()
                val premium = reader.readBool()
                val chase = reader.readBool() && chr.isGM
                if (chase) {
                    chr.chasing = true
                    chr.position = Point(reader.readInteger(), reader.readInteger())
                    println("pos: ${chr.position}")
                }

                portal.enter(chr)
            }
        }
    }
}