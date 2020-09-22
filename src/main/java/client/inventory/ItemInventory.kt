package client.inventory

import client.inventory.slots.ItemSlot

open class ItemInventory(var slotMax: Short) {

    val items: MutableMap<Short, ItemSlot> = HashMap()
}