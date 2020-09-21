package net.maple.handlers.net

import client.Client
import net.maple.handlers.PacketHandler
import util.HexTool.toHex
import util.packet.PacketReader

class ClientDumpLogHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        println("[ClientDumpLogHandler] " + toHex(reader.data))
    }
}