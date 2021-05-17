package net.maple.handlers.pet

import client.Client
import client.pet.FieldUserPet
import util.packet.PacketReader

class PetActionHandler : PetPacketHandler {

    override fun handlePetPacket(reader: PacketReader, c: Client, pet: FieldUserPet) {
        val timestamp = reader.readInteger()
        val type = reader.readByte()
        val action = reader.readByte()
        val msg = reader.readMapleString()

        c.character.field.broadcast(
            pet.getPetActionCommandPacket(type, action, success = true, chatBalloon = true),
            c.character
        )
    }
}