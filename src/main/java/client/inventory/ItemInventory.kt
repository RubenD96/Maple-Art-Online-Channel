package client.inventory

import client.inventory.item.slots.ItemSlot

open class ItemInventory(var slotMax: Byte) {

    val items: MutableMap<Short, ItemSlot> = HashMap()
}