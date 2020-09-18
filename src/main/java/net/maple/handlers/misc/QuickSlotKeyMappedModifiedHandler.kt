package net.maple.handlers.misc

import client.Client
import net.maple.handlers.PacketHandler
import util.packet.PacketReader

class QuickSlotKeyMappedModifiedHandler : PacketHandler() {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character
        for (i in 0..7) {
            chr.quickSlotKeys[i] = reader.readInteger()
        }
    }
}