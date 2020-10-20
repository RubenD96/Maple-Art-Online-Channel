package field.obj.drop

import client.Character
import field.obj.AbstractFieldObject
import field.obj.FieldObject
import net.maple.packets.FieldPackets.enterField
import net.maple.packets.FieldPackets.leaveField
import util.packet.Packet

abstract class AbstractFieldDrop(val owner: Int, val source: FieldObject, val questId: Int) : AbstractFieldObject() {

    var leaveType: Byte = 0x00
    var expire: Long = 0

    abstract val isMeso: Boolean
    abstract val info: Int

    abstract fun pickUp(chr: Character)

    override val enterFieldPacket: Packet get() = getEnterFieldPacket(EnterType.FFA)

    fun getEnterFieldPacket(enterType: Byte): Packet {
        return enterField(enterType)
    }

    override val leaveFieldPacket: Packet get() = getLeaveFieldPacket(null)

    fun getLeaveFieldPacket(chr: Character?): Packet {
        return leaveField(chr)
    }
}