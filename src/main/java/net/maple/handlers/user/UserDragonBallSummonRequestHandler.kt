package net.maple.handlers.user

import client.Client
import net.maple.handlers.PacketHandler
import util.packet.PacketReader

class UserDragonBallSummonRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        // todo make sure this is secure, before people start spamming the packet xD
        println("[UserDragonBallSummonRequestHandler] summon click :)")
    }
}