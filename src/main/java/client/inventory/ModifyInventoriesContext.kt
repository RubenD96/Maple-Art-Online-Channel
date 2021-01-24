package client.inventory

import client.inventory.item.slots.ItemSlot
import client.inventory.item.slots.ItemSlotBundle
import client.inventory.item.templates.ItemTemplate
import client.inventory.operations.AbstractModifyInventoryOperation
import util.packet.PacketWriter
import java.util.*

class ModifyInventoriesContext(inventories: Map<ItemInventoryType, ItemInventory>) {

    private val inventories: MutableMap<ItemInventoryType, ModifyInventoryContext> = EnumMap(ItemInventoryType::class.java)

    val operations: List<AbstractModifyInventoryOperation>
        get() {
            val operations: MutableList<AbstractModifyInventoryOperation> = ArrayList()
            inventories.values.stream().map { it.operations }.forEach { operations.addAll(it) }
            return operations
        }

    fun getInventoryContext(type: ItemInventoryType): ModifyInventoryContextInterface {
        return inventories[type]!!
    }

    fun getInventoryByItemId(id: Int): ModifyInventoryContextInterface {
        return getInventoryContext(ItemInventoryType.values()[id / 1000000 - 1])
    }

    fun add(item: ItemSlot) {
        getInventoryByItemId(item.templateId).add(item)
    }

    fun add(item: ItemTemplate, quantity: Short) {
        getInventoryByItemId(item.id).add(item, quantity)
    }

    operator fun set(slot: Short, item: ItemSlot) {
        getInventoryByItemId(item.templateId)[slot] = item
    }

    operator fun set(slot: Short, item: ItemTemplate, quantity: Short) {
        getInventoryByItemId(item.id)[slot, item] = quantity
    }

    fun remove(item: ItemSlot) {
        getInventoryByItemId(item.templateId).remove(item)
    }

    fun remove(item: ItemSlot, quantity: Short) {
        getInventoryByItemId(item.templateId).remove(item, quantity)
    }

    fun remove(id: Int, quantity: Short) {
        getInventoryByItemId(id).remove(id, quantity)
    }

    fun take(bundle: ItemSlotBundle, quantity: Short): ItemSlotBundle {
        return getInventoryByItemId(bundle.templateId).take(bundle, quantity)
    }

    fun take(id: Int, quantity: Short): ItemSlotBundle {
        return getInventoryByItemId(id).take(id, quantity)
    }

    fun update(item: ItemSlot) {
        getInventoryByItemId(item.templateId).update(item)
    }

    fun encode(pw: PacketWriter) {
        val operations = operations
        pw.writeByte(operations.size.toByte())
        operations.forEach { it.encode(pw) }
    }

    init {
        for ((key, value) in inventories) {
            this.inventories[key] = ModifyInventoryContext(key, value)
        }
    }
}