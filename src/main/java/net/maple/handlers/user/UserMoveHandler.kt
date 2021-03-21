package net.maple.handlers.user

import client.Client
import client.replay.MoveCollection.Movement
import constants.FieldConstants.JQ_FIELDS
import net.maple.handlers.PacketHandler
import net.maple.packets.CharacterPackets.move
import util.logging.LogType
import util.logging.Logger
import util.packet.PacketReader

class UserMoveHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        reader.readLong()
        val fieldKey = reader.readByte()
        if (fieldKey != chr.fieldKey) {
            return run {
                Logger.log(LogType.FIELD_KEY, "FieldKey mismatch", this, c)
                c.close(this, "FieldKey mismatch")
            }
        }
        reader.readLong()
        reader.readInteger()
        reader.readInteger()
        reader.readInteger()

        val path = chr.move(reader)

        if (JQ_FIELDS.contains(chr.fieldId)) {
            chr.moveCollections[chr.fieldId]?.movements?.add(Movement(System.currentTimeMillis(), reader.data))
            chr.times["jq start"] = System.currentTimeMillis()
        }

        chr.field.broadcast(chr.move(path), chr)
    }
}