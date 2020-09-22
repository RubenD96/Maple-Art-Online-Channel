package client.inventory.slots

abstract class ItemSlot {

    var templateId = 0
    var cashItemSN: Long = 0
    var expire: Long = 0
    var uuid: ByteArray? = null
    var isNewItem = true

    override fun toString(): String {
        return "ItemSlot{templateId=$templateId, cashItemSN=$cashItemSN, expire=$expire, uuid=${uuid.contentToString()}}"
    }
}