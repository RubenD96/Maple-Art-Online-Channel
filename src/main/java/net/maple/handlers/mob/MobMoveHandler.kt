package net.maple.handlers.mob

import client.Client
import field.obj.life.FieldMob
import field.obj.life.FieldNPC
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter
import java.util.stream.IntStream

class MobMoveHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val oid = reader.readInteger()
        val field = c.character.field

        if (field != null) { // might happen anytime user leaves the field but client still sent the packet when field was already set to null server sided
            val obj = field.getControlledObject(c.character, oid) ?: return
            if (obj is FieldNPC) {
                System.err.println("[MobMoveHandler] Packet sent from FieldNPC object: $oid")
                return
            }

            val mob = obj as FieldMob
            val mobCtrlSN = reader.readShort() // moveid
            val v7 = reader.readByte()
            val oldSplit = v7.toInt() and 0xF0 != 0
            val mobMoveStartResult = v7 > 0 // use skill //(v7 & 0xF) != 0;
            val curSplit = reader.readByte() // the skill
            val illegalVelocity = reader.readInteger()
            val v8 = reader.readByte()

            val cheatedRandom = v8.toInt() and 0xF0 != 0
            val cheatedCtrlMove = v8.toInt() and 0xF != 0

            val multiTargetForBall = reader.readInteger() // wtf
            IntStream.range(0, multiTargetForBall).forEach { i: Int -> reader.readLong() }

            val randTimeForAreaAttack = reader.readInteger()
            IntStream.range(0, randTimeForAreaAttack).forEach { i: Int -> reader.readInteger() }

            val unk1 = reader.readInteger() // HackedCode
            val unk2 = reader.readInteger()
            val unk3 = reader.readInteger() // HackedCodeCrc
            val unk4 = reader.readInteger()

            mob.controller?.write(sendMobControl(mob, mobCtrlSN, mobMoveStartResult))
            mob.field.broadcast(sendMobMovement(reader, mob, mobMoveStartResult, curSplit, illegalVelocity), mob.controller)
        }
    }

    companion object {
        private fun sendMobControl(mob: FieldMob, mobCtrlSN: Short, mobMoveStartResult: Boolean): Packet {
            val pw = PacketWriter(13)

            pw.writeHeader(SendOpcode.MOB_CTRL_ACK)
            pw.writeInt(mob.id)
            pw.writeShort(mobCtrlSN)
            pw.writeBool(mobMoveStartResult)
            pw.writeShort(0) // nMP
            pw.write(0) // skill command
            pw.write(0) // skill level

            return pw.createPacket()
        }

        private fun sendMobMovement(r: PacketReader, mob: FieldMob, mobMoveStartResult: Boolean, curSplit: Byte, illegalVelocity: Int): Packet {
            val pw = PacketWriter(32)

            pw.writeHeader(SendOpcode.MOB_MOVE)
            pw.writeInt(mob.id)
            pw.writeBool(false) // NotForceLandWhenDiscord
            pw.writeBool(mobMoveStartResult) // NotChangeAction
            pw.write(0) // NextAttackPossible
            pw.write(curSplit.toInt()) // Left
            pw.writeInt(illegalVelocity) // dwTargetInfo

            pw.writeInt(0) // MultiTargetForBall (counter) if > 0, + int int
            pw.writeInt(0) // RandTimeForAreaAttack (counter) if > 0, + int

            mob.move(r).encode(pw)

            return pw.createPacket()
        }
    }
}