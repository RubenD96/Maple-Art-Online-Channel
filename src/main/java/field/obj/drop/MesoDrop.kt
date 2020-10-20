package field.obj.drop

import client.Character
import client.messages.MesoDropPickUpMessage
import field.obj.FieldObject
import net.maple.packets.CharacterPackets.message

class MesoDrop(owner: Int, source: FieldObject, override val info: Int, questId: Int) : AbstractFieldDrop(owner, source, questId) {

    override val isMeso = true

    override fun pickUp(chr: Character) {
        leaveType = LeaveType.PICKUP
        field.leave(this, getLeaveFieldPacket(chr))
        chr.gainMeso(info)
        chr.message(MesoDropPickUpMessage(info))
    }
}