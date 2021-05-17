package net.maple.handlers.pet

import client.Client
import client.pet.FieldUserPet
import net.maple.SendOpcode
import util.packet.PacketReader
import util.packet.PacketWriter

class PetMoveHandler : PetPacketHandler {

    override fun handlePetPacket(reader: PacketReader, c: Client, pet: FieldUserPet) {
        val pw = PacketWriter(32)

        pw.writeHeader(SendOpcode.PET_MOVE)
        pw.writeInt(c.character.id)
        pw.writeByte(pet.idx)

        pet.move(reader).encode(pw)

        pet.field.broadcast(pw.createPacket(), c.character)
    }
}