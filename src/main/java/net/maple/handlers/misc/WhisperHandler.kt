package net.maple.handlers.misc

import client.Character
import client.Client
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import net.server.Server
import util.logging.LogType
import util.logging.Logger
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter

class WhisperHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val mode = reader.readByte()
        val timestamp = reader.readInteger()
        val target = reader.readMapleString()

        Server.getCharacter(target)?.let {
            when {
                mode.toInt() == WhisperFlag.LOCATION or WhisperFlag.REQUEST -> { // /find | /c
                    handleFind(c, it)
                }
                mode.toInt() == WhisperFlag.WHISPER or WhisperFlag.REQUEST -> {
                    val msg = reader.readMapleString()
                    if (msg.length > 75) return Logger.log(LogType.HACK, "Message $msg too long", this, c)

                    it.write(getWhisperMessagePacket(c.character.name, c.worldChannel.channelId, c.isAdmin, msg))
                    c.write(getWhisperSuccessPacket(target, true))
                }
                mode.toInt() == WhisperFlag.LOCATION_FRIEND or WhisperFlag.REQUEST -> {
                    handleFind(c, it, WhisperFlag.LOCATION_FRIEND)
                }
            }
        } ?: run {
            c.write(getWhisperSuccessPacket(target, false))
        }
    }

    private fun handleFind(c: Client, chr: Character, flag: Int = WhisperFlag.LOCATION) {
        when {
            chr.isInCashShop -> {
                c.write(getFindResult(chr, FindResultType.CASHSHOP, flag = flag))
            }
            chr.getChannel() == c.worldChannel -> {
                c.write(getFindResult(chr, FindResultType.SAME_CHANNEL, chr.fieldId, flag = flag))
            }
            else -> {
                c.write(getFindResult(chr, FindResultType.DIFFERENT_CHANNEL, chr.getChannel().channelId, flag = flag))
            }
        }
    }

    companion object {

        private fun getWhisperSuccessPacket(target: String, success: Boolean): Packet {
            val pw = PacketWriter(8)

            pw.writeHeader(SendOpcode.WHISPER)
            pw.write(WhisperFlag.WHISPER or WhisperFlag.RESULT)
            pw.writeMapleString(target)
            pw.writeBool(success)

            return pw.createPacket()
        }

        private fun getWhisperMessagePacket(target: String, channel: Int, fromAdmin: Boolean, message: String): Packet {
            val pw = PacketWriter(8)

            pw.writeHeader(SendOpcode.WHISPER)
            pw.write(WhisperFlag.WHISPER or WhisperFlag.RECIEVE)
            pw.writeMapleString(target)
            pw.writeByte(channel.toByte())
            pw.writeBool(fromAdmin)
            pw.writeMapleString(message)

            return pw.createPacket()
        }

        private fun getFindResult(target: Character, type: Byte, fieldOrChannel: Int = -1, flag: Int): Packet {
            val pw = PacketWriter(8)

            pw.writeHeader(SendOpcode.WHISPER)
            pw.write(flag or WhisperFlag.RESULT)
            pw.writeMapleString(target.name)
            pw.writeByte(type)
            pw.writeInt(fieldOrChannel)

            if (type == FindResultType.SAME_CHANNEL) {
                pw.writeInt(target.position.x)
                pw.writeInt(target.position.y)
            }

            return pw.createPacket()
        }

        private object FindResultType {
            const val ITC: Byte = 0x00
            const val SAME_CHANNEL: Byte = 0x01
            const val CASHSHOP: Byte = 0x02
            const val DIFFERENT_CHANNEL: Byte = 0x03
        }

        private object WhisperFlag {
            const val LOCATION = 0x01
            const val WHISPER = 0x02
            const val REQUEST = 0x04
            const val RESULT = 0x08
            const val RECIEVE = 0x10
            const val BLOCKED = 0x20
            const val LOCATION_FRIEND = 0x40
            const val MANAGER = 0x80
        }
    }
}