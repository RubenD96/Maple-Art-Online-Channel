package net.maple.handlers.user

import client.Character
import client.Client
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter

class UserEmotionHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        val emotion = reader.readInteger()
        val duration = reader.readInteger()
        val item = reader.readBool()

        chr.field.broadcast(sendEmotion(chr, emotion, duration, item), chr)
    }

    companion object {
        private fun sendEmotion(chr: Character, emotion: Int, duration: Int, item: Boolean): Packet {
            val pw = PacketWriter(15)

            pw.writeHeader(SendOpcode.USER_EMOTION)
            pw.writeInt(chr.id)
            pw.writeInt(emotion)
            pw.writeInt(duration)
            pw.writeBool(item)

            return pw.createPacket()
        }
    }
}