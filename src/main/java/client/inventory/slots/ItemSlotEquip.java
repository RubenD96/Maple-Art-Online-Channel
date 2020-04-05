package client.inventory.slots;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSlotEquip extends ItemSlot {

    private byte RUC, CUC;
    private short STR, DEX, INT, LUK, MaxHP, MaxMP, PAD, MAD, PDD, MDD, ACC, EVA, craft, speed, jump, attribute;
    private String Title;
    private byte levelUpType, level, grade, CHUC;
    private int EXP, durability, IUC;
    private short option1, option2, option3, socket1, socket2;
}
