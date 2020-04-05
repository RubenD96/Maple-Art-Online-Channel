package client.inventory.slots;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ItemSlot {

    protected int templateId;
    protected long cashItemSN;
    protected long expire;
}
