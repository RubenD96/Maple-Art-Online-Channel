package net.maple.handlers.misc

import client.Client
import field.obj.FieldObjectType
import field.obj.drop.AbstractFieldDrop
import net.maple.handlers.PacketHandler
import util.packet.PacketReader

class DropPickUpRequestHandler : PacketHandler() {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character
        reader.readByte()
        reader.readInteger() // timestamp?
        reader.readShort() // start of position?
        reader.readShort()
        val id = reader.readInteger()
        reader.readInteger()

        val drop = chr.field.getObject(FieldObjectType.DROP, id) as AbstractFieldDrop? ?: return
        drop.pickUp(chr)
    }
}