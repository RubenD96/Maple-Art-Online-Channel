package field.obj.reactor

import field.obj.AbstractFieldObject
import field.obj.FieldObjectType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.maple.SendOpcode
import util.packet.Packet
import util.packet.PacketWriter
import util.packet.Reader
import java.awt.Point

class FieldReactor(val template: ReactorTemplate) : AbstractFieldObject() {

    var time = 0
    var f = false
    var name = ""
    var actionDelay: Short = 0
    var stateEnd: Long = 0
        set(value) {
            field = System.currentTimeMillis() + actionDelay + value + 120
        }

    var state = 0
        set(value) {
            field = value
            changeState()
        }

    override val fieldObjectType = FieldObjectType.REACTOR

    override val enterFieldPacket: Packet
        get() {
            val pw = PacketWriter(18)

            pw.writeHeader(SendOpcode.REACTOR_ENTER_FIELD)
            pw.writeInt(id)
            pw.writeInt(template.id)
            pw.write(state) // state
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

    private fun changeState() {
        sendChangeStatePacket()

        if (state >= template.events.size) {
            val reactor = this
            GlobalScope.launch {
                //field.leave(reactor, leaveFieldPacket)
                delay(time * 50L)
                state = 0
                field.enter(reactor)
            }
        }
    }

    private fun sendChangeStatePacket() {
        val pw = PacketWriter(16)

        pw.writeHeader(SendOpcode.REACTOR_CHANGE_STATE)
        pw.writeInt(id)
        pw.write(state)
        pw.writePosition(position)
        pw.writeShort(actionDelay) // hitStart? aniState?
        pw.write(0) // properEventIdx
        //pw.write(((stateEnd - System.currentTimeMillis() + 99) / 100).toInt()) // stateEnd
        pw.write(4)

        //field.broadcast(pw.createPacket())
    }

    fun decode(reader: Reader) {
        position = Point(reader.readInteger(), reader.readInteger())
        time = reader.readInteger()
        f = reader.readBool()
        name = reader.readMapleString()
    }
}