package net.maple.handlers.user

import client.Client
import client.pet.FieldUserPet
import client.inventory.ItemInventoryType
import client.inventory.item.slots.ItemSlotPet
import client.player.StatType
import net.maple.handlers.PacketHandler
import net.maple.packets.CharacterPackets.statUpdate
import util.packet.PacketReader

class UserActivatePetRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        val timestamp = reader.readInteger()
        val position = reader.readShort()
        val leader = reader.readBool()

        if (!chr.getInventory(ItemInventoryType.CASH).items.containsKey(position)) return

        val item = chr.getInventory(ItemInventoryType.CASH).items[position] as? ItemSlotPet ?: return
        var pet = chr.pets.firstOrNull { it.item == item }

        if (pet != null) {
            val id = pet.idx

            chr.pets.remove(pet)
            chr.pets.filter { it.idx > id }.forEach { it.idx-- }

            chr.field.broadcast(pet.leaveFieldPacket)
            pet.idx = -1
        } else {
            if (chr.pets.size >= 3) {
                chr.enableActions()
                return
            }

            val id: Byte = if (leader) 0 else chr.pets.size.toByte()

            pet = FieldUserPet(chr, item)
            pet.idx = id
            chr.pets.filter { it.idx >= id }.forEach { it.idx++ }
            chr.pets.add(pet)

            chr.field.broadcast(pet.enterFieldPacket)
        }

        chr.statUpdate(mutableListOf(StatType.PET, StatType.PET2, StatType.PET3), true)
    }
}