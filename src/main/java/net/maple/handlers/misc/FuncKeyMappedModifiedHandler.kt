package net.maple.handlers.misc

import client.Client
import client.player.key.KeyBinding
import net.maple.handlers.PacketHandler
import util.packet.PacketReader

class FuncKeyMappedModifiedHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        reader.readInteger() // bunch of zeroes

        val changed = reader.readInteger()
        for (i in 0 until changed) {
            val key = reader.readInteger()
            val type = reader.readByte()
            val action = reader.readInteger()
            chr.keyBindings[key] = KeyBinding(type, action)
        }
    }
}