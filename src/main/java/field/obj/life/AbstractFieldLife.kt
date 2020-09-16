package field.obj.life

import field.obj.AbstractFieldObject
import field.movement.MovePath
import util.packet.PacketReader

abstract class AbstractFieldLife : AbstractFieldObject(), FieldLife {

    override var moveAction: Byte = 0
    override var foothold: Short = 0

    fun move(packet: PacketReader): MovePath {
        val path = MovePath(packet)
        path.apply(this)
        return path
    }
}