package net.maple.handlers.group

import client.Client
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter

class GroupMessageHandler : PacketHandler {

    private object ChatGroupType {
        const val FRIEND: Byte = 0x00
        const val PARTY: Byte = 0x01
        const val GUILD: Byte = 0x02
        const val ALLIANCE: Byte = 0x03
        const val COUPLE: Byte = 0x04
        const val TO_COUPLE: Byte = 0x05
        const val EXPEDITION: Byte = 0x06
    }

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character
        reader.readInteger() // timestamp
        val type = reader.readByte() // chat type
        val size = reader.readByte().toInt() // count members
        for (i in 0 until size) {
            reader.readInteger() // cid
        }
        val message = reader.readMapleString() // the message

        when (type) {
            ChatGroupType.FRIEND -> {
                chr.friendList.sendMessage(multiChat(ChatGroupType.FRIEND, chr.name, message))
            }
            ChatGroupType.PARTY -> {
                val party = chr.party ?: return
                party.sendMessage(multiChat(ChatGroupType.PARTY, chr.name, message), chr.id)
            }
            ChatGroupType.GUILD -> {
                val guild = chr.guild ?: return
                guild.broadcast(multiChat(ChatGroupType.GUILD, chr.name, message), chr)
            }
            else -> println("Unknown GroupMessageHandler type ($type) from ${chr.name}")
        }
    }

    companion object {
        fun multiChat(type: Byte, name: String, message: String): Packet {
            val pw = PacketWriter(8)

            pw.writeHeader(SendOpcode.GROUP_MESSAGE)
            pw.write(type.toInt())
            pw.writeMapleString(name)
            pw.writeMapleString(message)

            return pw.createPacket()
        }
    }
}