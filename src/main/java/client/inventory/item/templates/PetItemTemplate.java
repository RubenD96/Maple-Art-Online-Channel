package client.inventory.item.templates;

import client.inventory.slots.ItemSlotPet;
import lombok.Getter;
import util.packet.PacketReader;

public class PetItemTemplate extends ItemTemplate {

    @Getter private int life;

    public PetItemTemplate(int id, PacketReader r) {
        super(id, r);
        life = r.readShort();
    }

    public ItemSlotPet toItemSlot() {
        ItemSlotPet pet = new ItemSlotPet();
        pet.setTemplateId(getId());
        if (life > 0) {
            //pet.setDateDead(); todo do something i guess
        }
        return pet;
    }
}
