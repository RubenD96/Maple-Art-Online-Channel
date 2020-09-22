package field.obj.reactor

import field.obj.AbstractFieldObject
import field.obj.FieldObjectType
import net.maple.SendOpcode
import util.packet.Packet
import util.packet.PacketWriter
import util.packet.Reader
import java.awt.Point

class FieldReactor(val rid: Int) : AbstractFieldObject() {

    var time = 0
    var f = false
    var name = ""

    var state = 0
        set(value) {
            field = value
            sendChangeStatePacket()
        }

    override val fieldObjectType = FieldObjectType.REACTOR

    override val enterFieldPacket: Packet
        get() {
            val pw = PacketWriter(18)

            pw.writeHeader(SendOpcode.REACTOR_ENTER_FIELD)
            pw.writeInt(id)
            pw.writeInt(rid)
            pw.write(0) // state
            pw.writePosition(position)
            pw.writeBool(f)
            pw.writeMapleString(name)

            return pw.createPacket()
        }

    override val leaveFieldPacket: Packet
        get() {
            val pw = PacketWriter(11)

            pw.writeHeader(SendOpcode.REACTOR_LEAVE_FIELD)
            pw.writeInt(id)
            pw.write(0) // state
            pw.writePosition(position)

            return pw.createPacket()
        }

    private fun sendChangeStatePacket() {
        val pw = PacketWriter(16)

        pw.writeHeader(SendOpcode.REACTOR_CHANGE_STATE)
        pw.writeInt(id)
        pw.write(state)
        pw.writePosition(position)
        pw.writeShort(393)
        pw.write(0) // properEventIdx
        pw.write(0) // stateEnd

        field.broadcast(pw.createPacket())
    }

    fun decode(reader: Reader) {
        position = Point(reader.readInteger(), reader.readInteger())
        time = reader.readInteger()
        f = reader.readBool()
        name = reader.readMapleString()
    }
}