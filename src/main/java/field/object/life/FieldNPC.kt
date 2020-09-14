package field.`object`.life

import field.`object`.FieldObjectType
import net.maple.SendOpcode
import util.packet.Packet
import util.packet.PacketWriter

class FieldNPC : AbstractFieldControlledLife {

    val npcId: Int
    var isMove = false

    constructor(npcId: Int) {
        this.npcId = npcId
    }

    constructor(npc: FieldNPC) {
        npcId = npc.npcId
        name = npc.name
        isMove = npc.isMove
    }

    override val fieldObjectType = FieldObjectType.NPC

    // obj id
    override val enterFieldPacket: Packet
        get() {
            val pw = PacketWriter(22)

            pw.writeHeader(SendOpcode.NPC_ENTER_FIELD)
            pw.writeInt(id) // obj id
            pw.writeInt(npcId)
            pw.writePosition(position)
            pw.write(if (f) 1 else 0)
            pw.writeShort(foothold)
            pw.writeShort(rx0)
            pw.writeShort(rx1)
            pw.writeBool(!hide)

            return pw.createPacket()
        }

    // obj id
    override val leaveFieldPacket: Packet
        get() {
            val pw = PacketWriter(6)

            pw.writeHeader(SendOpcode.NPC_LEAVE_FIELD)
            pw.writeInt(id) // obj id

            return pw.createPacket()
        }

    override fun getChangeControllerPacket(setAsController: Boolean): Packet {
        val pw = PacketWriter(7)

        pw.writeHeader(SendOpcode.NPC_CHANGE_CONTROLLER)
        pw.writeBool(setAsController)
        pw.writeInt(id) // obj id

        return pw.createPacket()
    }

    override fun toString(): String {
        return "FieldNPC{npcId=$npcId, move=$isMove, rx0=$rx0, rx1=$rx1, cy=$cy, name='$name', hide=$hide, f=$f, foothold=$foothold, position=$position, id=$id}"
    }
}