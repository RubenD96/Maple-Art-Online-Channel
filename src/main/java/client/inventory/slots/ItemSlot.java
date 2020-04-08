package client.inventory.slots;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public abstract class ItemSlot implements Cloneable {

    protected int templateId;
    protected long cashItemSN;
    protected long expire;
    protected byte[] uuid;
    private boolean newItem = true;

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
