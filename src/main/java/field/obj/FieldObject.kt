package field.obj

import field.Field
import field.obj.drop.AbstractFieldDrop
import util.packet.Packet
import java.awt.Point
import kotlin.reflect.KClass

interface FieldObject {

    var id: Int
    var field: Field
    var position: Point
    val enterFieldPacket: Packet
    val leaveFieldPacket: Packet

    val kclass: KClass<out FieldObject>
        get() {
            if (this is AbstractFieldDrop) return AbstractFieldDrop::class
            return this::class
        }
}