package net.maple.handlers.user.attack

import client.Character
import client.Client
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import util.logging.LogType
import util.logging.Logger
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter
import java.util.*

class UserAttackHandler(private val type: AttackType) : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character
        val fieldKey = reader.readByte()
        if (fieldKey != chr.fieldKey) {
            return run {
                Logger.log(LogType.FIELD_KEY, "FieldKey mismatch", this, c)
            }
        }

        val info = AttackInfo(type, chr, reader)
        info.decode()

        chr.field.broadcast(showAttack(chr, info), chr)
        info.apply()
    }

    private fun showAttack(chr: Character, info: AttackInfo): Packet {
        val pw = PacketWriter(32)

        pw.writeMapleHeader((SendOpcode.USER_MELEE_ATTACK.value + type.type).toShort())
        pw.writeInt(chr.id)
        pw.write(info.damagePerMob or 16 * info.mobCount)
        pw.write(chr.level)

        if (info.skillId > 0) {
            pw.write(chr.skills[info.skillId]?.level ?: 0)
            pw.writeInt(info.skillId)
        } else {
            pw.write(0)
        }

        pw.writeByte(info.option)
        pw.writeShort(info.actionAndDir)
        //pw.writeShort(info.action and 0x7FFF or (if (info.isLeft) 1 else 0) shl 15)

        if (info.action <= 0x110) {
            pw.write(0) // nActionSpeed
            pw.write(0) // nMastery
            pw.writeInt(0) // nBulletItemID
            info.damageInfo.forEach {
                pw.writeInt(it.mobId)

                if (it.mobId <= 0) return@forEach

                pw.write(it.hitAction.toInt())
                Arrays.stream(it.damage).forEach { damage: Int ->
                    pw.writeBool(false) // abCritical
                    pw.writeInt(damage)
                }
            }
        }

        if (type == AttackType.SHOOT) {
            pw.writeShort(0)
            pw.writeShort(0)
        }

        // pw.writeInt(0); // keydown
        return pw.createPacket()
    }
}