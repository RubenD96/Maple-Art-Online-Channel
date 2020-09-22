package net.maple.handlers.misc

import client.Client
import field.obj.FieldObjectType
import field.obj.reactor.FieldReactor
import net.maple.handlers.PacketHandler
import util.packet.PacketReader

class ReactorHitHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val oid = reader.readInteger()
        reader.readInteger() // hardcoded 0 in the client
        val dwHitOption = reader.readInteger()
        val tDelay = reader.readShort()
        reader.readInteger() // another hardcoded 0

        println("oid: $oid, dwHitOption: $dwHitOption, tDelay: $tDelay")

        val reactor = c.character.field.getObject(FieldObjectType.REACTOR, oid) as FieldReactor? ?: return

        reactor.state++
    }
}