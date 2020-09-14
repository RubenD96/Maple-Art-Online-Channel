package field.`object`.drop

import client.Character
import client.inventory.slots.ItemSlot
import client.messages.ItemDropPickUpMessage
import field.`object`.FieldObject
import net.maple.packets.CharacterPackets

class ItemDrop(owner: Int, source: FieldObject, private val item: ItemSlot, questId: Int) : AbstractFieldDrop(owner, source, questId) {

    override val isMeso = false
    override val info = item.templateId

    override fun pickUp(chr: Character) {
        leaveType = LeaveType.PICKUP
        field.leave(this, getLeaveFieldPacket(chr))
        CharacterPackets.modifyInventory(chr, { it.add(item) }, true)
        chr.write(CharacterPackets.message(ItemDropPickUpMessage(item.templateId)))
    }
}