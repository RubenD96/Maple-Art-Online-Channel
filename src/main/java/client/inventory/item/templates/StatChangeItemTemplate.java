package client.inventory.item.templates;

import client.inventory.item.flags.StatFlag;
import lombok.Getter;
import util.packet.PacketReader;

@Getter
public class StatChangeItemTemplate extends ItemBundleTemplate {

    private int consumeFlags;
    private int HP, MP, HPR, MPR;
    private boolean noCancelMouse;
    private short PAD, PDD, MAD, MDD, ACC, EVA, craft, speed, jump, morph;
    private int time;

    public StatChangeItemTemplate(int id, PacketReader r) {
        super(id, r);
        consumeFlags = r.readInteger();

        if (containsFlag(StatFlag.HP)) HP = r.readInteger();
        if (containsFlag(StatFlag.MP)) MP = r.readInteger();
        if (containsFlag(StatFlag.HPR)) HPR = r.readInteger();
        if (containsFlag(StatFlag.MPR)) MPR = r.readInteger();
        if (containsFlag(StatFlag.NO_CANCEL_MOUSE)) noCancelMouse = r.readBool();
        if (containsFlag(StatFlag.PAD)) PAD = r.readShort();
        if (containsFlag(StatFlag.PDD)) PDD = r.readShort();
        if (containsFlag(StatFlag.MAD)) MAD = r.readShort();
        if (containsFlag(StatFlag.MDD)) MDD = r.readShort();
        if (containsFlag(StatFlag.ACC)) ACC = r.readShort();
        if (containsFlag(StatFlag.EVA)) EVA = r.readShort();
        if (containsFlag(StatFlag.CRAFT)) craft = r.readShort();
        if (containsFlag(StatFlag.SPEED)) speed = r.readShort();
        if (containsFlag(StatFlag.JUMP)) jump = r.readShort();
        if (containsFlag(StatFlag.MORPH)) morph = r.readShort();
        if (containsFlag(StatFlag.TIME)) time = r.readInteger();
    }

    public boolean containsFlag(StatFlag flag) {
        return (consumeFlags & flag.getValue()) == flag.getValue();
    }

    @Override
    public String toString() {
        System.out.println(super.toString());
        return "StatChangeItemTemplate{" +
                "consumeFlags=" + consumeFlags +
                "HP=" + HP +
                ", MP=" + MP +
                ", HPR=" + HPR +
                ", MPR=" + MPR +
                ", noCancelMouse=" + noCancelMouse +
                ", PAD=" + PAD +
                ", PDD=" + PDD +
                ", MAD=" + MAD +
                ", MDD=" + MDD +
                ", ACC=" + ACC +
                ", EVA=" + EVA +
                ", craft=" + craft +
                ", speed=" + speed +
                ", jump=" + jump +
                ", morph=" + morph +
                ", time=" + time +
                '}';
    }
}