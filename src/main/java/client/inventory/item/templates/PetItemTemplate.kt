package client.inventory.item.templates

import client.inventory.item.slots.ItemSlotPet
import util.packet.PacketReader

class PetItemTemplate(id: Int, r: PacketReader) : ItemTemplate(id, r) {

    val life: Int

    init {
        life = r.readShort().toInt()
    }

    override fun toItemSlot(): ItemSlotPet {
        val pet = ItemSlotPet()
        pet.templateId = id

        if (life > 0) {
            //pet.setDateDead(); todo do something i guess
        }
        return pet
    }
}