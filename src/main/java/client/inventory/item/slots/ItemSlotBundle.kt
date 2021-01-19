package client.inventory.item.slots

import java.util.*

class ItemSlotBundle : ItemSlot() {

    var number by getObservableValue<Short>(0)
    var maxNumber by getObservableValue<Short>(0)
    var attribute by getObservableValue<Short>(0)
    var title by getObservableValue("")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ItemSlotBundle
        return templateId == that.templateId && maxNumber == that.maxNumber && attribute == that.attribute && title == that.title && cashItemSN == that.cashItemSN && expire == that.expire
    }

    override fun hashCode(): Int {
        return Objects.hash(templateId, maxNumber, attribute, title, cashItemSN, expire)
    }

    override fun toString(): String {
        return "${super.toString()}\nItemSlotBundle{number=$number, maxNumber=$maxNumber, attribute=$attribute, title='$title'}"
    }
}