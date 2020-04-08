package client.inventory.item.templates;

import client.inventory.ItemVariation;
import client.inventory.ItemVariationType;
import client.inventory.item.flags.EquipFlag;
import client.inventory.slots.ItemSlotEquip;
import lombok.Getter;
import util.Rand32;
import util.packet.PacketReader;

@Getter
public class ItemEquipTemplate extends ItemTemplate {

    private int equipFlags;
    private short ReqSTR, ReqDEX, ReqINT, ReqLUK, ReqFAME, ReqJob;
    private byte ReqLevel, TUC;
    private short IncSTR, IncDEX, IncINT, IncLUK;
    private int IncMaxHP, IncMaxMP, IncMaxHPr, IncMaxMPr;
    private short IncPAD, IncMAD, IncPDD, IncMDD, IncACC, IncEVA, IncCraft, IncSpeed, IncJump;
    private boolean onlyEquip, tradeBlockEquip, notExtend, sharableOnce;
    private byte appliableKarmaType;
    private int setItemId;
    private int durability = -1;

    public ItemEquipTemplate(int id, PacketReader r) {
        super(id, r);
        equipFlags = r.readInteger();

        ReqSTR = r.readShort();
        ReqDEX = r.readShort();
        ReqINT = r.readShort();
        ReqLUK = r.readShort();
        ReqFAME = r.readShort();
        ReqJob = r.readShort();
        ReqLevel = r.readByte();

        if (containsFlag(EquipFlag.TUC)) TUC = r.readByte();
        if (containsFlag(EquipFlag.INC_STR)) IncSTR = r.readShort();
        if (containsFlag(EquipFlag.INC_DEX)) IncDEX = r.readShort();
        if (containsFlag(EquipFlag.INC_INT)) IncINT = r.readShort();
        if (containsFlag(EquipFlag.INC_LUK)) IncLUK = r.readShort();
        if (containsFlag(EquipFlag.INC_MAX_HP)) IncMaxHP = r.readInteger();
        if (containsFlag(EquipFlag.INC_MAX_MP)) IncMaxMP = r.readInteger();
        if (containsFlag(EquipFlag.INC_MAX_HPR)) IncMaxHPr = r.readInteger();
        if (containsFlag(EquipFlag.INC_MAX_MPR)) IncMaxMPr = r.readInteger();
        if (containsFlag(EquipFlag.INC_PAD)) IncPAD = r.readShort();
        if (containsFlag(EquipFlag.INC_MAD)) IncMAD = r.readShort();
        if (containsFlag(EquipFlag.INC_PDD)) IncPDD = r.readShort();
        if (containsFlag(EquipFlag.INC_MDD)) IncMDD = r.readShort();
        if (containsFlag(EquipFlag.INC_ACC)) IncACC = r.readShort();
        if (containsFlag(EquipFlag.INC_EVA)) IncEVA = r.readShort();
        if (containsFlag(EquipFlag.INC_CRAFT)) IncCraft = r.readShort();
        if (containsFlag(EquipFlag.INC_SPEED)) IncSpeed = r.readShort();
        if (containsFlag(EquipFlag.INC_JUMP)) IncJump = r.readShort();
        if (containsFlag(EquipFlag.ONLY_EQUIP)) onlyEquip = r.readBool();
        if (containsFlag(EquipFlag.TRADE_BLOCK_EQUIP)) tradeBlockEquip = r.readBool();
        if (containsFlag(EquipFlag.NOT_EXTEND)) notExtend = r.readBool();
        if (containsFlag(EquipFlag.SHARABLE_ONCE)) sharableOnce = r.readBool();
        if (containsFlag(EquipFlag.APPLIABLE_KARMA_TYPE)) appliableKarmaType = r.readByte();
        if (containsFlag(EquipFlag.SET_ITEM_ID)) setItemId = r.readInteger();
        if (containsFlag(EquipFlag.DURABILITY)) durability = r.readInteger();
    }

    public boolean containsFlag(EquipFlag flag) {
        return (equipFlags & flag.getValue()) == flag.getValue();
    }

    public ItemSlotEquip fromDbToSlot(
            int str, int dex, int int_, int luk, int hp, int mp,
            int pad, int mad, int pdd, int mdd, int acc, int eva,
            int speed, int jump, int craft, int durability
    ) {
        ItemSlotEquip equip = new ItemSlotEquip();
        equip.setTemplateId(getId());
        equip.setRUC(TUC);
        equip.setSTR((short) str);
        equip.setDEX((short) dex);
        equip.setINT((short) int_);
        equip.setLUK((short) luk);
        equip.setMaxHP((short) hp);
        equip.setMaxMP((short) mp);
        equip.setPAD((short) pad);
        equip.setMAD((short) mad);
        equip.setPDD((short) pdd);
        equip.setMDD((short) mdd);
        equip.setACC((short) acc);
        equip.setEVA((short) eva);
        equip.setSpeed((short) speed);
        equip.setJump((short) jump);
        equip.setCraft((short) craft);
        equip.setDurability(durability);

        return equip;
    }

    public ItemSlotEquip toItemSlot(ItemVariationType type) {
        ItemVariation variation = new ItemVariation(Rand32.Create(), type);
        ItemSlotEquip equip = new ItemSlotEquip();
        equip.setTemplateId(getId());
        equip.setRUC(TUC);
        equip.setSTR((short) variation.get(IncSTR));
        equip.setDEX((short) variation.get(IncDEX));
        equip.setINT((short) variation.get(IncINT));
        equip.setLUK((short) variation.get(IncLUK));
        equip.setMaxHP((short) variation.get(IncMaxHP));
        equip.setMaxMP((short) variation.get(IncMaxMP));
        equip.setPAD((short) variation.get(IncPAD));
        equip.setMAD((short) variation.get(IncMAD));
        equip.setPDD((short) variation.get(IncPDD));
        equip.setMDD((short) variation.get(IncMDD));
        equip.setACC((short) variation.get(IncACC));
        equip.setEVA((short) variation.get(IncEVA));
        equip.setCraft((short) variation.get(IncCraft));
        equip.setSpeed((short) variation.get(IncSpeed));
        equip.setJump((short) variation.get(IncJump));
        equip.setDurability(100);
        return equip;
    }

    @Override
    public String toString() {
        System.out.println(super.toString());
        return "ItemEquipTemplate{" +
                "equipFlags=" + equipFlags +
                "ReqSTR=" + ReqSTR +
                ", ReqDEX=" + ReqDEX +
                ", ReqINT=" + ReqINT +
                ", ReqLUK=" + ReqLUK +
                ", ReqFAME=" + ReqFAME +
                ", ReqJob=" + ReqJob +
                ", ReqLevel=" + ReqLevel +
                ", TUC=" + TUC +
                ", IncSTR=" + IncSTR +
                ", IncDEX=" + IncDEX +
                ", IncINT=" + IncINT +
                ", IncLUK=" + IncLUK +
                ", IncMaxHP=" + IncMaxHP +
                ", IncMaxMP=" + IncMaxMP +
                ", IncMaxHPr=" + IncMaxHPr +
                ", IncMaxMPr=" + IncMaxMPr +
                ", IncPAD=" + IncPAD +
                ", IncMAD=" + IncMAD +
                ", IncPDD=" + IncPDD +
                ", IncMDD=" + IncMDD +
                ", IncACC=" + IncACC +
                ", IncEVA=" + IncEVA +
                ", IncCraft=" + IncCraft +
                ", IncSpeed=" + IncSpeed +
                ", IncJump=" + IncJump +
                ", onlyEquip=" + onlyEquip +
                ", tradeBlockEquip=" + tradeBlockEquip +
                ", notExtend=" + notExtend +
                ", sharableOnce=" + sharableOnce +
                ", appliableKarmaType=" + appliableKarmaType +
                ", setItemId=" + setItemId +
                ", durability=" + durability +
                '}';
    }
}
