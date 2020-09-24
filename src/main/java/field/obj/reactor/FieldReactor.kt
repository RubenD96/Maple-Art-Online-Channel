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
    var fieldName = "" // an attribute in Map.wz/reactor, theres a diff name in Reactor.wz

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
            pw.write(0) // state
            pw.writePosition(position)
            pw.writeBool(f)
            pw.writeMapleString(fieldName)

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

        if (state >= template.maxState) {
            field.leave(this, leaveFieldPacket)

            val reactor = this
            GlobalScope.launch {
                val start = System.currentTimeMillis()
                println("Waiting for reactor ${template.id} to respawn ($time)")
                delay(time.toLong())
                field.enter(reactor)
                println("Reactor ${template.id} respawned after ${System.currentTimeMillis() - start} milliseconds")
            }
        }
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
        fieldName = reader.readMapleString()
    }
}