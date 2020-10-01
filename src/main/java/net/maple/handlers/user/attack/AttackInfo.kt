package net.maple.handlers.user.attack

import client.Character
import util.packet.PacketReader
import java.util.stream.IntStream

class AttackInfo(val type: AttackType, val chr: Character, val r: PacketReader) {

    var damagePerMob = 0
    var mobCount = 0
    var skillId = 0
    val keyDown = 0
    var action = 0
    var attackTime = 0
    var option: Byte = 0
    var attackActionType: Byte = 0
    var attackSpeed: Byte = 0
    var isLeft = false
    var damageInfo: MutableList<DamageInfo> = ArrayList()

    fun decode() {
        r.readByte()
        r.readInteger()
        r.readInteger()

        val v6 = r.readByte()
        damagePerMob = v6.toInt() and 0x0F
        mobCount = v6.toInt() shr 4

        r.readInteger()
        r.readInteger()

        skillId = r.readInteger()
        r.readByte()

        if (type == AttackType.MAGIC) {
            IntStream.range(0, 6).forEachOrdered { r.readInteger() }
        }

        r.readInteger()
        r.readInteger()
        r.readInteger()
        r.readInteger()
        // r.readInteger(); // keydown
        option = r.readByte()

        if (type == AttackType.SHOOT) {
            r.readByte()
        }

        val v17 = r.readShort()
        isLeft = v17.toInt() shr 15 and 1 != 0
        action = v17.toInt() and 0xFFF
        //println(action)

        r.readInteger()

        attackActionType = r.readByte()
        //println(attackActionType)
        attackSpeed = r.readByte()
        attackTime = r.readInteger()

        r.readInteger()

        if (type == AttackType.SHOOT) {
            r.readShort()
            r.readShort()
            r.readByte()
            // shadow stars: readint
        }

        IntStream.range(0, mobCount).forEach {
            val info = DamageInfo(type, chr)
            info.decode(r, damagePerMob)
            damageInfo.add(info)
        }
    }

    fun apply() {
        damageInfo.forEach { it.apply() }
    }
}