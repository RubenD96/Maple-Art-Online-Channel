package client.inventory.slots;

import java.util.Arrays;

public abstract class ItemSlot implements Cloneable {

    protected int templateId;
    protected long cashItemSN;
    protected long expire;
    protected byte[] uuid;
    private boolean newItem = true;

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public long getCashItemSN() {
        return cashItemSN;
    }

    public void setCashItemSN(long cashItemSN) {
        this.cashItemSN = cashItemSN;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public byte[] getUuid() {
        return uuid;
    }

    public void setUuid(byte[] uuid) {
        this.uuid = uuid;
    }

    public boolean isNewItem() {
        return newItem;
    }

    public void setNewItem(boolean newItem) {
        this.newItem = newItem;
    }

    @Override
    public String toString() {
        return "ItemSlot{" +
                "templateId=" + templateId +
                ", cashItemSN=" + cashItemSN +
                ", expire=" + expire +
                ", uuid=" + Arrays.toString(uuid) +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
