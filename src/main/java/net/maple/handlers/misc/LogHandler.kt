package net.maple.handlers.misc

import client.Client
import net.maple.handlers.PacketHandler
import util.logging.LogType
import util.logging.Logger
import util.packet.PacketReader

class LogHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val log = reader.readMapleString()
        Logger.log(LogType.ADMIN_COMMAND, log, this, c)
    }
}