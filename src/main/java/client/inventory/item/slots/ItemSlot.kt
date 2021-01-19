package client.inventory.item.slots

import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

abstract class ItemSlot {

    protected fun <T> getObservableValue(default: T): ReadWriteProperty<Any?, T> {
        return Delegates.observable(default) { _, _, _ -> updated = true }
    }

    var templateId = 0
    var cashItemSN: Long = 0
    var expire: Long = 0
    var uuid: ByteArray? = null
    var isNewItem = true
    var updated = false

    override fun toString(): String {
        return "ItemSlot{templateId=$templateId, cashItemSN=$cashItemSN, expire=$expire, uuid=${uuid?.contentToString()}}"
    }
}