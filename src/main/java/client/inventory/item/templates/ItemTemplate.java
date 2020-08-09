package client.inventory.item.templates;

import client.inventory.ItemVariationType;
import client.inventory.item.flags.ItemFlag;
import client.inventory.slots.ItemSlot;
import lombok.Getter;
import util.packet.PacketReader;

@Getter
public class ItemTemplate {

    protected int flags;
    private int id, sellPrice;
    private boolean timeLimited, quest, partyQuest, only, tradeBlock, notSale, bigSize, expireOnLogout, accountSharable, cash;

    public ItemTemplate(int id, PacketReader r) {
        flags = r.readInteger();
        this.id = id;

        if (containsFlag(ItemFlag.PRICE)) sellPrice = r.readInteger();
        if (containsFlag(ItemFlag.TIME_LIMITED)) timeLimited = r.readBool();
        if (containsFlag(ItemFlag.QUEST)) quest = r.readBool();
        if (containsFlag(ItemFlag.PARTY_QUEST)) partyQuest = r.readBool();
        if (containsFlag(ItemFlag.ONLY)) only = r.readBool();
        if (containsFlag(ItemFlag.TRADE_BLOCK)) tradeBlock = r.readBool();
        if (containsFlag(ItemFlag.NOT_SALE)) notSale = r.readBool();
        if (containsFlag(ItemFlag.BIG_SIZE)) bigSize = r.readBool();
        if (containsFlag(ItemFlag.EXPIRE_ON_LOGOUT)) expireOnLogout = r.readBool();
        if (containsFlag(ItemFlag.ACCOUNT_SHARE)) accountSharable = r.readBool();
        if (containsFlag(ItemFlag.CASH)) cash = r.readBool();
    }

    public boolean containsFlag(ItemFlag flag) {
        return (flags & flag.getValue()) == flag.getValue();
    }

    @Override
    public String toString() {
        return "ItemTemplate{" +
                "flags=" + flags +
                ", id=" + id +
                ", sellPrice=" + sellPrice +
                ", timeLimited=" + timeLimited +
                ", quest=" + quest +
                ", partyQuest=" + partyQuest +
                ", only=" + only +
                ", tradeBlock=" + tradeBlock +
                ", notSale=" + notSale +
                ", bigSize=" + bigSize +
                ", expireOnLogout=" + expireOnLogout +
                ", accountSharable=" + accountSharable +
                ", cash=" + cash +
                '}';
    }

    public ItemSlot toItemSlot() {
        return toItemSlot(ItemVariationType.NONE);
    }

    public ItemSlot toItemSlot(ItemVariationType type) {
        if (this instanceof ItemEquipTemplate) {
            return ((ItemEquipTemplate) this).toItemSlot(type);
        } else if (this instanceof ItemBundleTemplate) {
            return ((ItemBundleTemplate) this).toItemSlot();
        } else if (this instanceof PetItemTemplate) {
            return ((PetItemTemplate) this).toItemSlot();
        }
        throw new IllegalArgumentException();
    }
}
