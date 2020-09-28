package client.replay

import client.SimpleCharacter
import field.obj.FieldObjectType
import util.packet.Packet

// todo...
class Replay : SimpleCharacter() {
    override var face: Int
        get() = TODO("Not yet implemented")
        set(value) {}
    override var hair: Int
        get() = TODO("Not yet implemented")
        set(value) {}
    override var gender: Int
        get() = TODO("Not yet implemented")
        set(value) {}
    override var skinColor: Int
        get() = TODO("Not yet implemented")
        set(value) {}
    override var fieldId: Int
        get() = TODO("Not yet implemented")
        set(value) {}
    override val fieldObjectType: FieldObjectType
        get() = TODO("Not yet implemented")
    override val enterFieldPacket: Packet
        get() = TODO("Not yet implemented")
    override val leaveFieldPacket: Packet
        get() = TODO("Not yet implemented")
}