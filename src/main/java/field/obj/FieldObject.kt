package field.obj

import field.Field
import util.packet.Packet
import java.awt.Point

interface FieldObject {
    var id: Int
    var field: Field
    var position: Point
    val enterFieldPacket: Packet
    val leaveFieldPacket: Packet
}