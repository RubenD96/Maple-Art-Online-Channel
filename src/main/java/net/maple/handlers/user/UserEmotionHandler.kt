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

    /**
     * EMT_BLINK = 0x0,
     * EMT_HIT = 0x1,
     * EMT_SMILE = 0x2,
     * EMT_TROUBLED = 0x3,
     * EMT_CRY = 0x4,
     * EMT_ANGRY = 0x5,
     * EMT_BEWILDERED = 0x6,
     * EMT_STUNNED = 0x7,
     * EMT_VOMIT = 0x8,
     * EMT_OOPS = 0x9,
     * EMT_CHEERS = 0xA,
     * EMT_CHU = 0xB,
     * EMT_WINK = 0xC,
     * EMT_PAIN = 0xD,
     * EMT_GLITTER = 0xE,
     * EMT_BLAZE = 0xF,
     * EMT_SHINE = 0x10,
     * EMT_LOVE = 0x11,
     * EMT_DESPAIR = 0x12,
     * EMT_HUM = 0x13,
     * EMT_BOWING = 0x14,
     * EMT_HOT = 0x15,
     * EMT_DAM = 0x16,
     * EMT_QBLUE = 0x17,
     * EMT_NO = 0x18,
     */
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