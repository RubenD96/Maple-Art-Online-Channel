package field.obj.drop

import client.Character
import client.inventory.item.slots.ItemSlot
import client.messages.ItemDropPickUpMessage
import field.obj.FieldObject
import net.maple.packets.CharacterPackets.message
import net.maple.packets.CharacterPackets.modifyInventory

class ItemDrop(owner: Int, source: FieldObject, private val item: ItemSlot, questId: Int) : AbstractFieldDrop(owner, source, questId) {

    override val isMeso = false
    override val info = item.templateId

    override fun pickUp(chr: Character) {
        leaveType = LeaveType.PICKUP
        field.leave(this, getLeaveFieldPacket(chr))
        chr.modifyInventory({ it.add(item) }, true)
        chr.message(ItemDropPickUpMessage(item.templateId))
    }
}