package client.inventory.item.templates

import client.inventory.item.slots.ItemSlotPet
import util.packet.PacketReader

class PetItemTemplate(id: Int) : ItemTemplate(id) {

    var life: Int = 0
        private set

    override fun decode(r: PacketReader): PetItemTemplate {
        super.decode(r)

        life = r.readShort().toInt()

        return this
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