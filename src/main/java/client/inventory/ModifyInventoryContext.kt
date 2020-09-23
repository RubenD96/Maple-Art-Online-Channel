package client.inventory

import client.inventory.item.templates.ItemTemplate
import client.inventory.operations.*
import client.inventory.item.slots.ItemSlot
import client.inventory.item.slots.ItemSlotBundle
import constants.ItemConstants.isRechargeableItem
import managers.ItemManager.getItem
import util.packet.PacketWriter
import java.util.*
import java.util.stream.IntStream
import kotlin.math.max

class ModifyInventoryContext : ModifyInventoryContextInterface {

    private val type: ItemInventoryType
    private val inventory: ItemInventory
    val operations: Queue<AbstractModifyInventoryOperation>

    constructor(type: ItemInventoryType, inventory: ItemInventory) {
        this.type = type
        this.inventory = inventory
        operations = LinkedList()
    }

    constructor(inventory: ItemInventory) {
        type = ItemInventoryType.EQUIP
        this.inventory = inventory
        operations = LinkedList()
    }

    override fun add(item: ItemSlot) {
        if (item is ItemSlotBundle) {
            if (item.number < 1) item.number = 1.toShort()
            if (item.maxNumber < 1) item.maxNumber = 1.toShort()

            val template = getItem(item.templateId) ?: return
            while (item.number > item.maxNumber) {
                item.number = (item.number - item.maxNumber).toShort()
                add(template, item.maxNumber)
            }

            val mergeable: ItemSlotBundle? = inventory.items.values.stream()
                    .filter { it is ItemSlotBundle }
                    .filter {
                        val b = it as ItemSlotBundle
                        item.number + b.number <= b.maxNumber
                    }.filter { it == item }
                    .findFirst().orElse(null) as ItemSlotBundle?

            if (mergeable != null) {
                val quantity = item.number + mergeable.number
                val maxNumber = mergeable.maxNumber

                if (quantity > maxNumber) {
                    val leftOver = quantity - maxNumber
                    item.number = leftOver.toShort()
                    mergeable.number = maxNumber
                    updateQuantity(mergeable)
                    add(item)
                    return
                }

                mergeable.number = (mergeable.number + item.number).toShort()
                updateQuantity(mergeable)
                return
            }
        }

        val slot = IntStream.range(1, inventory.slotMax.toInt())
                .filter {
                    val s = it.toShort()
                    inventory.items[s] == null
                }
                .findFirst().orElse(0).toShort()
        set(slot, item)
    }

    override fun add(item: ItemTemplate, quantity: Short) {
        val i = item.toItemSlot()
        if (i is ItemSlotBundle) {
            i.number = quantity
        }
        add(i)
    }

    override fun set(slot: Short, item: ItemSlot) {
        inventory.items[slot] = item
        operations.add(AddInventoryOperation(type, slot, item))
    }

    override fun set(slot: Short, item: ItemTemplate, quantity: Short) {
        val i = item.toItemSlot()
        if (i is ItemSlotBundle) {
            i.number = quantity
        }
        set(slot, i)
    }

    override fun remove(slot: Short) {
        inventory.items.remove(slot)
        operations.add(RemoveInventoryOperation(type, slot))
    }

    override fun remove(slot: Short, quantity: Short) {
        val item = inventory.items[slot]

        if (item is ItemSlotBundle) {
            if (!isRechargeableItem(item.templateId)) {
                if (quantity > 0) {
                    item.number = (item.number - quantity).toShort()
                    item.number = max(0, item.number.toInt()).toShort()

                    if (item.number > 0) {
                        updateQuantity(item)
                        return
                    }
                }
            }
        }

        remove(slot)
    }

    override fun remove(item: ItemSlot) {
        remove(inventory.items.entries
                .stream()
                .filter { it.value === item }
                .map { it.key }.findFirst().get())
    }

    override fun remove(item: ItemSlot, quantity: Short) {
        remove(inventory.items.entries
                .stream()
                .filter { it.value === item }
                .map { it.key }.findFirst().get(), quantity)
    }

    override fun remove(id: Int, quantity: Short) {
        var removed = 0

        inventory.items.values.stream()
                .filter { it.templateId == id }
                .forEach {
                    println(it)
                    if (removed >= quantity) return@forEach

                    if (it is ItemSlotBundle) {
                        if (!isRechargeableItem(it.templateId)) {
                            val difference = quantity - removed

                            if (it.number > difference) {
                                removed += difference
                                it.number = (it.number - difference).toShort()
                                updateQuantity(it)
                            } else {
                                removed += it.number.toInt()
                                remove(it)
                            }
                        }
                    } else {
                        removed++
                        remove(it)
                    }
                }
    }

    override fun move(from: Short, to: Short) {
        val item = inventory.items[from] ?: return

        if (item is ItemSlotBundle) {
            if (!isRechargeableItem(item.templateId) &&
                    inventory.items.containsKey(to) &&
                    inventory.items[to] is ItemSlotBundle) {
                val existing = inventory.items[to] as ItemSlotBundle?

                if (item == existing) {
                    val quantity = item.number + existing.number
                    val max = existing.maxNumber.toInt()

                    if (quantity > max) {
                        val left = quantity - max
                        item.number = left.toShort()
                        existing.number = max.toShort()
                        updateQuantity(item)
                    } else {
                        existing.number = quantity.toShort()
                        remove(item)
                    }
                    updateQuantity(existing)
                    return
                }
            }
        }

        inventory.items.remove(from)
        inventory.items[to]?.let {
            inventory.items[from] = it
        }

        inventory.items[to] = item
        operations.add(MoveInventoryOperation(type, from, to))
    }

    override fun take(slot: Short, quantity: Short): ItemSlotBundle {
        val bundle = inventory.items[slot] as ItemSlotBundle
        val newBundle = ItemSlotBundle()

        newBundle.templateId = bundle.templateId
        newBundle.number = quantity
        newBundle.maxNumber = bundle.maxNumber
        newBundle.attribute = bundle.attribute
        newBundle.title = bundle.title
        newBundle.expire = bundle.expire
        newBundle.cashItemSN = bundle.cashItemSN

        remove(bundle, quantity)

        return newBundle
    }

    override fun take(bundle: ItemSlotBundle, quantity: Short): ItemSlotBundle {
        return take(bundle.templateId, quantity)
    }

    override fun take(id: Int, quantity: Short): ItemSlotBundle {
        return take(inventory.items.entries
                .stream()
                .filter { it.value.templateId == id }
                .map { it.key }.findFirst().get(), quantity)
    }

    override fun update(slot: Short) {
        val item = inventory.items[slot]
        remove(slot)
        set(slot, item!!)
    }

    override fun update(item: ItemSlot) {
        update(inventory.items.entries
                .stream()
                .filter { it.value === item }
                .map { it.key }.findFirst().get())
    }

    override fun encode(pw: PacketWriter) {
        pw.writeByte(operations.size.toByte())
        operations.forEach { it.encode(pw) }
    }

    private fun updateQuantity(item: ItemSlot) {
        updateQuantity(inventory.items.entries
                .stream()
                .filter { it.value === item }
                .map { it.key }.findFirst().get())
    }

    private fun updateQuantity(slot: Short) {
        operations.add(UpdateQuantityInventoryOperation(type, slot, (inventory.items[slot] as ItemSlotBundle?)!!.number))
    }
}