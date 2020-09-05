package client.inventory.slots;

import java.util.Objects;

public class ItemSlotBundle extends ItemSlot {

    private short number, maxNumber, attribute;
    private String title = "";

    public short getNumber() {
        return number;
    }

    public void setNumber(short number) {
        this.number = number;
    }

    public short getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(short maxNumber) {
        this.maxNumber = maxNumber;
    }

    public short getAttribute() {
        return attribute;
    }

    public void setAttribute(short attribute) {
        this.attribute = attribute;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    @Override
    public String toString() {
        return super.toString() + "\nItemSlotBundle{" +
                "number=" + number +
                ", maxNumber=" + maxNumber +
                ", attribute=" + attribute +
                ", title='" + title + '\'' +
                '}';
    }
}
