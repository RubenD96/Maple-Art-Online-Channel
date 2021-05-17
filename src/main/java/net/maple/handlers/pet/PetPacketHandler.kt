package net.maple.handlers.pet

import client.Client
import client.pet.FieldUserPet
import net.maple.handlers.PacketHandler
import util.packet.PacketReader

interface PetPacketHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val sn = reader.readLong()
        val pet = c.character.pets.firstOrNull { it.item.cashItemSN == sn } ?: return

        handlePetPacket(reader, c, pet)
    }

    fun handlePetPacket(reader: PacketReader, c: Client, pet: FieldUserPet)
}