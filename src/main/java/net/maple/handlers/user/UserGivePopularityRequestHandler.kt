package net.maple.handlers.user

import client.Character
import client.Client
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter

class UserGivePopularityRequestHandler : PacketHandler {

    private enum class GivePopularityRes(val value: Int) {
        SUCCESS(0x00),
        INVALID_CHARACTER_ID(0x01),
        LEVEL_LOW(0x02),
        ALREADY_DONE_TODAY(0x03),
        ALREADY_DONE_TARGET(0x04),
        NOTIFY(0x05),
        UNKNOWN_ERROR(-0x1);
    }

    override fun handlePacket(reader: PacketReader, c: Client) {
        val sender = c.character
        val cid = reader.readInteger()

        if (cid == sender.id) {
            c.close(this, c.character.name + " tried to fame themselves")
            return
        }

        if (sender.level < 15) {
            c.write(levelLowPacket)
            return
        }

        val fame = reader.readByte()
        val receiver = sender.field.getObject<Character>(cid) ?: return c.write(invalidCharacterIdPacket)

        if (fame < 0 || fame > 1) {
            c.close(this, "Invalid byte")
            return
        }

        if (fame.toInt() == 0) {
            receiver.fame--
        } else {
            receiver.fame++
        }

        c.write(getSuccessPacket(receiver, fame.toInt()))
        receiver.write(getNotifyPacket(sender, fame.toInt()))
    }

    companion object {
        private fun getSendPopPacket(operation: GivePopularityRes): PacketWriter {
            val pw = PacketWriter(8)

            pw.writeHeader(SendOpcode.GIVE_POPULARITY_RESULT)
            pw.write(operation.value)

            return pw
        }

        private fun getSuccessPacket(target: Character, fame: Int): Packet {
            val pw = getSendPopPacket(GivePopularityRes.SUCCESS)

            pw.writeMapleString(target.name)
            pw.write(fame)
            pw.writeInt(target.fame) // nPop

            return pw.createPacket()
        }

        private val invalidCharacterIdPacket: Packet
            get() {
                val pw = getSendPopPacket(GivePopularityRes.INVALID_CHARACTER_ID)
                return pw.createPacket()
            }

        private val levelLowPacket: Packet
            get() {
                val pw = getSendPopPacket(GivePopularityRes.LEVEL_LOW)
                return pw.createPacket()
            }

        // todo
        private val alreadyDoneTodayPacket: Packet
            get() {
                val pw = getSendPopPacket(GivePopularityRes.ALREADY_DONE_TODAY)
                return pw.createPacket()
            }

        // todo
        private val alreadyDoneTargetPacket: Packet
            get() {
                val pw = getSendPopPacket(GivePopularityRes.ALREADY_DONE_TARGET)
                return pw.createPacket()
            }

        private fun getNotifyPacket(sender: Character, fame: Int): Packet {
            val pw = getSendPopPacket(GivePopularityRes.NOTIFY)

            pw.writeMapleString(sender.name)
            pw.write(fame)

            return pw.createPacket()
        }
    }
}