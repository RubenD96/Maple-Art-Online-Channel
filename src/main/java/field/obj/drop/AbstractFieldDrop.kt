package field.obj.drop

import client.Character
import field.obj.AbstractFieldObject
import field.obj.FieldObject
import net.maple.packets.FieldPackets.enterField
import net.maple.packets.FieldPackets.leaveField
import util.packet.Packet
import java.awt.Point

abstract class AbstractFieldDrop(val owner: Int, val source: FieldObject, val questId: Int) : AbstractFieldObject() {

    override var position: Point = Point()
        set(value) {
            val fh = this.field.template.footholds.getFootholdUnderneath(value.x, value.y - 10)
            var ypos: Double = fh.y1().toDouble()
            if (fh.isSlope) {
                val slope = (fh.y2() - fh.y1()).toDouble() / (fh.x2() - fh.x1()).toDouble()
                ypos = slope * value.x.toDouble() - slope * fh.x1().toDouble() + fh.y1().toDouble()
            }
            field = Point(value.x, ypos.toInt())
        }

    var leaveType: Byte = 0x00
    var expire: Long = 0

    abstract val isMeso: Boolean
    abstract val info: Int

    abstract fun pickUp(chr: Character)

    override val enterFieldPacket: Packet get() = getEnterFieldPacket(EnterType.FFA)

    fun getEnterFieldPacket(enterType: Byte, cursedObject: Int? = null): Packet {
        return enterField(enterType, cursedObject)
    }

    override val leaveFieldPacket: Packet get() = getLeaveFieldPacket(null)

    fun getLeaveFieldPacket(chr: Character?): Packet {
        return leaveField(chr)
    }
}