package field.object.drop;

import client.Character;
import client.inventory.slots.ItemSlot;
import field.object.FieldObject;
import lombok.NonNull;

public class ItemFieldDrop extends AbstractFieldDrop {

    private final ItemSlot item;

    public ItemFieldDrop(@NonNull byte enterType, @NonNull byte leaveType, @NonNull int owner, @NonNull FieldObject source, ItemSlot item) {
        super(enterType, leaveType, owner, source);
        this.item = item;
    }

    @Override
    public boolean isMeso() {
        return false;
    }

    @Override
    public int getInfo() {
        return item.getTemplateId();
    }

    @Override
    public void pickUp(Character chr) {
        field.leave(this);
    }
}
