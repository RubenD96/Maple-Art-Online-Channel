package client.inventory

import client.inventory.item.slots.ItemSlot

open class ItemInventory(var slotMax: Short) {

    val items: MutableMap<Short, ItemSlot> = HashMap()
}