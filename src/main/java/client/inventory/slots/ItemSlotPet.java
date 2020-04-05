package client.inventory.slots;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSlotPet extends ItemSlot {

    private String petName;
    private byte level, repleteness;
    private short tameness, petAttribute, petSkill, attribute;
    private long dateDead;
    private int remainLife;
}
