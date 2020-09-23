package client.inventory.operations

import client.inventory.ItemInventoryType
import client.inventory.ModifyInventoryOperationType
import client.inventory.item.slots.ItemSlot
import net.maple.packets.ItemPackets.encode
import util.packet.PacketWriter

class AddInventoryOperation(inventory: ItemInventoryType, slot: Short, private val item: ItemSlot) : AbstractModifyInventoryOperation(inventory, slot) {

    override val type = ModifyInventoryOperationType.ADD

    override fun encodeData(pw: PacketWriter) {
        item.encode(pw)
    }
}