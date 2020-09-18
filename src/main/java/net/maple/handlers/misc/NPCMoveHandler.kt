package net.maple.handlers.misc

import client.Client
import field.obj.life.FieldNPC
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter

class NPCMoveHandler : PacketHandler() {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character
        val npcObjectId = reader.readInteger()

        val field = chr.field

        if (field != null) { // might happen anytime user leaves the field but client still sent the packet when field was already set to null server sided
            val npc = field.getControlledObject(chr, npcObjectId)
            if (npc is FieldNPC) {
                field.broadcast(moveNPC(npc, reader))
            }
        }
    }

    companion object {
        fun moveNPC(npc: FieldNPC, r: PacketReader): Packet {
            val pw = PacketWriter(8)

            pw.writeHeader(SendOpcode.NPC_MOVE)
            pw.writeInt(npc.id)
            pw.writeByte(r.readByte())
            pw.writeByte(r.readByte())

            if (npc.isMove) {
                npc.move(r).encode(pw)
            }

            return pw.createPacket()
        }
    }
}