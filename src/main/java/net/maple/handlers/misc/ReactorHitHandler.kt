package net.maple.handlers.misc

import client.Client
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

        val reactor = c.character.field.getObject<FieldReactor>(oid) ?: return

        reactor.actionDelay = tDelay
        //tStateEnd is tCur (current time) + tActionDelay (the tDelay from your handler) + pTemplate.tMoveDelay (parsed from the reactor if it exists) + GetHitDelay(nEventIdx)
        reactor.stateEnd = 0
        reactor.state++
    }
}