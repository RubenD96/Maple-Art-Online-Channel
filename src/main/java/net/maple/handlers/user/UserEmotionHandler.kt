package net.maple.handlers.user

import client.Avatar
import client.Client
import client.replay.MoveCollection.Emote
import constants.FieldConstants.JQ_FIELDS
import constants.ItemConstants.BASE_EMOTE
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import util.logging.LogType
import util.logging.Logger
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter

class UserEmotionHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        val emotion = reader.readInteger()
        val duration = reader.readInteger() // always -1?
        val item = reader.readBool() // always false?

        if (emotion > 7) { // item emote
            if (chr.getItemQuantity(BASE_EMOTE + (emotion - 8)) == 0) {
                Logger.log(LogType.INVALID, "Emote item not in possession", this, c)
                c.close(this, "Emote item not in possession")
                return
            }
        }

        if (JQ_FIELDS.contains(chr.fieldId)) {
            chr.moveCollections[chr.fieldId]?.emotes?.add(Emote(System.currentTimeMillis(), emotion))
        }

        chr.field.broadcast(sendEmotion(chr, emotion, duration, item), chr)
    }

    companion object {
        fun sendEmotion(avatar: Avatar, emotion: Int, duration: Int, item: Boolean): Packet {
            val pw = PacketWriter(15)

            pw.writeHeader(SendOpcode.USER_EMOTION)
            pw.writeInt(avatar.id)
            pw.writeInt(emotion)
            pw.writeInt(duration)
            pw.writeBool(item)

            return pw.createPacket()
        }
    }
}