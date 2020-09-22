package client.inventory

import client.inventory.item.templates.ItemTemplate
import client.inventory.slots.ItemSlot
import client.inventory.slots.ItemSlotBundle
import util.packet.PacketWriter

interface ModifyInventoryContextInterface {
    fun add(item: ItemSlot)
    fun add(item: ItemTemplate, quantity: Short)
    operator fun set(slot: Short, item: ItemSlot)
    operator fun set(slot: Short, item: ItemTemplate, quantity: Short)
    fun remove(slot: Short)
    fun remove(slot: Short, quantity: Short)
    fun remove(item: ItemSlot)
    fun remove(item: ItemSlot, quantity: Short)
    fun remove(id: Int, quantity: Short)
    fun move(from: Short, to: Short)
    fun take(slot: Short, quantity: Short): ItemSlotBundle
    fun take(bundle: ItemSlotBundle, quantity: Short): ItemSlotBundle
    fun take(id: Int, quantity: Short): ItemSlotBundle
    fun update(slot: Short)
    fun update(item: ItemSlot)
    fun encode(pw: PacketWriter)
}