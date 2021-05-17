package net.maple.handlers.pet

import client.Client
import client.pet.FieldUserPet
import util.packet.PacketReader

class PetInteractionRequestHandler : PetPacketHandler {

    /**
     * C9 00 D7 23 A3 01 00 00 00 00 00 00
     * C9 00 D7 23 A3 01 00 00 00 00 00 00
     */
    override fun handlePetPacket(reader: PacketReader, c: Client, pet: FieldUserPet) {

    }
}