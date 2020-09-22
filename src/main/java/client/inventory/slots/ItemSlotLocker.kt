package client.inventory.slots

import client.Client
import constants.ItemConstants
import util.packet.PacketWriter
import java.util.*

class ItemSlotLocker(val item: ItemSlot) {

    var commodityId = 0
    var paybackRate = 0
    var discountRate = 0
    var buyCharacterName: String? = null

    fun encode(c: Client, pw: PacketWriter) {
        pw.writeLong(item.cashItemSN)
        pw.writeInt(c.accId)
        pw.writeInt(c.character.id)
        pw.writeInt(item.templateId)
        pw.writeInt(commodityId)
        pw.writeShort(if (item is ItemSlotBundle) item.number else 1)
        pw.writeFixedString(buyCharacterName ?: "", 13)
        pw.writeLong(ItemConstants.PERMANENT) // todo?
        pw.writeInt(paybackRate)
        pw.writeInt(discountRate)
    }

    init {
        item.cashItemSN = Random().nextInt(Int.MAX_VALUE).toLong()
    }
}