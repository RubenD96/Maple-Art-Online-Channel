package net.maple.handlers.user

import client.Avatar
import client.Client
import client.command.CommandHandler
import client.replay.MoveCollection
import constants.FieldConstants
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter

class UserChatHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        reader.readInteger() // ?

        val msg = reader.readMapleString()
        val textBox = !reader.readBool()

        if ((msg.startsWith("!") && chr.isGM) || msg.startsWith("@")) {
            CommandHandler.executeCommand(chr, msg)
            return
        }

        chr.field.broadcast(chr.sendMessage(msg, textBox, chr.isGM), null)

        if (FieldConstants.JQ_FIELDS.contains(chr.fieldId)) {
            chr.moveCollections[chr.fieldId]?.chats?.add(MoveCollection.Chat(System.currentTimeMillis(), msg))
        }
    }

    companion object {
        fun Avatar.sendMessage(msg: String, textBox: Boolean, isGM: Boolean): Packet {
            val pw = PacketWriter(16)

            pw.writeHeader(SendOpcode.USER_CHAT)
            pw.writeInt(id)
            pw.writeBool(isGM)
            pw.writeMapleString(msg)
            pw.writeBool(!textBox)

            return pw.createPacket()
        }
    }
}