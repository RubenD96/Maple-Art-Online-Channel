package client.inventory.item.templates;

import client.inventory.ItemVariation;
import client.inventory.ItemVariationType;
import client.inventory.slots.ItemSlotEquip;
import client.inventory.item.flags.EquipFlag;
import lombok.Getter;
import util.Rand32;
import util.packet.PacketReader;

@Getter
public class ItemEquipTemplate extends ItemTemplate {

    private short ReqSTR, ReqDEX, ReqINT, ReqLUK, ReqFAME, ReqJob;
    private byte ReqLevel, TUC;
    private short IncSTR, IncDEX, IncINT, IncLUK;
    private int IncMaxHP, IncMaxMP, IncMaxHPr, IncMaxMPr;
    private short IncPAD, IncMAD, IncPDD, IncMDD, IncACC, IncEVA, IncCraft, IncSpeed, IncJump;
    private boolean onlyEquip, tradeBlockEquip, notExtend, sharableOnce;
    private byte appliableKarmaType;
    private int setItemId, durability;

    public ItemEquipTemplate(int id, PacketReader r) {
        super(id, r);
        ReqSTR = r.readShort();
        ReqDEX = r.readShort();
        ReqINT = r.readShort();
        ReqLUK = r.readShort();
        ReqFAME = r.readShort();
        ReqJob = r.readShort();
        ReqLevel = r.readByte();
        ReqSTR = r.readShort();
        ReqSTR = r.readShort();

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
        return (flags & flag.getValue()) == flag.getValue();
    }

    public ItemSlotEquip toItemSlot(ItemVariationType type) {
        ItemVariation variation = new ItemVariation(Rand32.Create(), type);
        ItemSlotEquip equip = new ItemSlotEquip();
        equip.setTemplateId(getId());
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
}
