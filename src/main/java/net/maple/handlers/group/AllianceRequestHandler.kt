package net.maple.handlers.group

import client.Client
import net.maple.handlers.PacketHandler
import net.maple.packets.AlliancePackets
import util.packet.PacketReader

class AllianceRequestHandler : PacketHandler {

    private companion object AllianceReq {
        const val CREATE: Byte = 0x00
        const val LOAD: Byte = 0x01
        const val WITHDRAW: Byte = 0x02
        const val INVITE: Byte = 0x03
        const val JOIN: Byte = 0x4
        const val UPDATE_MEMBER_COUNT_MAX: Byte = 0x05
        const val KICK: Byte = 0x06
        const val CHANGE_MASTER: Byte = 0x07
        const val SET_GRADE_NAME: Byte = 0x08
        const val CHANGE_GRADE: Byte = 0x09
        const val SET_NOTICE: Byte = 0x0A
        const val DESTROY: Byte = 0x0B
    }

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        when (val mode = reader.readByte()) {
            LOAD -> AlliancePackets.load(chr, chr.guild?.alliance)
            else -> System.err.println("[AllianceRequestHandler] Unhandled op $mode")
        }
    }
}