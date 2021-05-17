package net.maple.packets

import client.Avatar
import client.Character
import client.effects.FieldEffectInterface
import field.Field
import field.obj.FieldObject
import field.obj.drop.AbstractFieldDrop
import field.obj.drop.EnterType
import net.maple.SendOpcode
import net.maple.packets.CharacterPackets.encodeData
import net.maple.packets.CharacterPackets.encodeLooks
import util.packet.Packet
import util.packet.PacketWriter
import kotlin.math.ceil

object FieldPackets {

    fun Character.setField(): Packet {
        val pw = PacketWriter(32)

        pw.writeHeader(SendOpcode.SET_FIELD)
        pw.writeShort(0)
        pw.writeInt(this.getChannel().channelId)
        pw.writeInt(0) // world

        fieldKey = ceil(Math.random() * 255).toInt().toByte()
        pw.writeByte(fieldKey)
        pw.writeBool(migrating)
        pw.writeShort(0) // chatblock?

        if (migrating) {
            migrating = false
            pw.writeInt(0) // calc seed
            pw.writeInt(0) // calc seed
            pw.writeInt(0) // chatblock?

            encodeData(pw)

            pw.writeInt(0)
            pw.writeInt(0)
            pw.writeInt(0)
            pw.writeInt(0)
        } else {
            pw.writeByte(0)
            pw.writeInt(fieldId)
            pw.writeByte(portal)
            pw.writeInt(health)

            pw.writeBool(chasing)
            if (chasing) {
                chasing = false
                pw.writeInt(position.x)
                pw.writeInt(position.y)
            }
        }

        pw.writeLong(System.currentTimeMillis() * 10000 + 116444592000000000L)
        return pw.createPacket()
    }

    fun Avatar.enterField(): Packet {
        val pw = PacketWriter(32)

        pw.writeHeader(SendOpcode.USER_ENTER_FIELD)
        pw.writeInt(this.id) // obj id
        pw.write(this.level)
        pw.writeMapleString(this.name)

        // guild
        this.guild?.let {
            pw.writeMapleString(it.name)
            it.mark?.encode(pw) ?: pw.write(ByteArray(6))
        } ?: pw.writeMapleString(if (this is Character) "" else "Replay").write(ByteArray(6))

        // temp stats
        // masks
        pw.writeInt(0)
        pw.writeInt(0)
        pw.writeInt(0)
        pw.writeInt(0)

        // nDefenseAtt & nDefenseState
        pw.write(0)
        pw.write(0)

        pw.writeShort(this.job)

        encodeLooks(pw, false)

        pw.writeInt(0)
        pw.writeInt(0)
        pw.writeInt(0)
        pw.writeInt(0)
        pw.writeInt(0) // complete set itemid

        pw.writeInt(this.portableChair ?: 0)

        pw.writePosition(this.position)
        pw.write(this.moveAction.toInt())
        pw.writeShort(this.foothold)
        pw.write(0) // ?

        pets.forEach {
            pw.writeBool(true)
            it.encodeData(pw)
        }

        // a whole bunch of ?
        pw.writeBool(false)
        pw.writeInt(0)
        pw.writeInt(0)
        pw.writeInt(0)
        pw.write(0)
        pw.writeBool(false)
        pw.writeBool(false)
        pw.writeBool(false)
        pw.writeBool(false)
        pw.write(0)
        pw.write(0)
        pw.writeInt(0)

        return pw.createPacket()
    }

    fun Avatar.leaveField(): Packet {
        val pw = PacketWriter(6)

        pw.writeHeader(SendOpcode.USER_LEAVE_FIELD)
        pw.writeInt(this.id)

        return pw.createPacket()
    }

    fun AbstractFieldDrop.enterField(enterType: Byte, cursedObject: Int? = null): Packet {
        val pw = PacketWriter(32)

        //byte type = drop.getEnterType();
        val source = this.source

        pw.writeHeader(SendOpcode.DROP_ENTER_FIELD)
        pw.write(enterType.toInt())
        pw.writeInt(this.id)
        pw.writeBool(this.isMeso)
        pw.writeInt(cursedObject ?: this.info)
        pw.writeInt( /*drop.getOwner()*/0)
        pw.write( /*type*/0x02) // own type
        pw.writePosition(this.position)
        pw.writeInt(if (source is Character) 0 else source.id) // source

        if (enterType != EnterType.FFA) {
            pw.writePosition(source.position)
            pw.writeShort(0) // delay
        }

        if (!this.isMeso) {
            pw.writeLong( /*drop.getExpire() * 10000 + 116444592000000000L*/0)
        }

        pw.writeBool(false)
        pw.writeBool(false)

        return pw.createPacket()
    }

    fun AbstractFieldDrop.leaveField(source: FieldObject? = null): Packet {
        val pw = PacketWriter(14)

        pw.writeHeader(SendOpcode.DROP_LEAVE_FIELD)
        pw.write(this.leaveType.toInt()) // nLeaveType
        pw.writeInt(this.id)

        if (this.leaveType.toInt() == 0x02 || this.leaveType.toInt() == 0x03 || this.leaveType.toInt() == 0x05) {
            pw.writeInt(source?.id ?: 0)
        } else if (this.leaveType.toInt() == 0x04) {
            pw.writeShort(0)
        }

        return pw.createPacket()
    }

    fun Field.fieldEffect(effect: FieldEffectInterface) {
        val pw = PacketWriter(12)

        pw.writeHeader(SendOpcode.FIELD_EFFECT)
        effect.encode(pw)

        this.broadcast(pw.createPacket())
    }
}