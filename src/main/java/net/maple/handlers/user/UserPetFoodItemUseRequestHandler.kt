package net.maple.handlers.user

import client.Client
import client.inventory.ItemInventoryType
import client.inventory.item.slots.ItemSlotBundle
import net.maple.handlers.PacketHandler
import net.maple.packets.CharacterPackets.modifyInventory
import util.packet.PacketReader

class UserPetFoodItemUseRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character
        if (chr.pets.size == 0) return chr.enableActions()

        val timestamp = reader.readInteger()
        val position = reader.readShort()
        val itemId = reader.readInteger()

        var toFeed = chr.pets[0]

        if (chr.pets.size > 1 && chr.pets[1].item.repleteness < toFeed.item.repleteness) toFeed = chr.pets[1]
        if (chr.pets.size > 2 && chr.pets[2].item.repleteness < toFeed.item.repleteness) toFeed = chr.pets[2]

        val slot = chr.getInventory(ItemInventoryType.CONSUME).items[position] ?: return
        if (slot.templateId / 10000 != 212 || slot.templateId != itemId) return

        toFeed.onEatFood(30)
        chr.modifyInventory({ it.remove(slot as ItemSlotBundle, 1) }, true)
    }
}