package client.inventory.slots;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class ItemSlotBundle extends ItemSlot {

    private short number, maxNumber, attribute;
    private String title = "";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemSlotBundle that = (ItemSlotBundle) o;
        return templateId == that.templateId &&
                maxNumber == that.maxNumber &&
                attribute == that.attribute &&
                title.equals(that.title) &&
                cashItemSN == that.cashItemSN &&
                expire == that.expire;
    }

    @Override
    public int hashCode() {
        return Objects.hash(templateId, maxNumber, attribute, title, cashItemSN, expire);
    }
}
