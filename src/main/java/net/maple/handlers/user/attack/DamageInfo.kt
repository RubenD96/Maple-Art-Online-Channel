package net.maple.handlers.user.attack

import client.Character
import field.obj.FieldObjectType
import field.obj.life.FieldMob
import util.packet.PacketReader
import java.awt.Point
import java.util.*
import java.util.stream.IntStream

class DamageInfo(val type: AttackType, val chr: Character) {

    var mobId = 0
    var foreAction = 0
    var calcDamageStatIndex = 0
    var hitAction: Byte = 0
    var frameIdx: Byte = 0
    var isLeft = false
    var isDoomed = false
    var delay: Short = 0

    lateinit var hitPosition: Point
    lateinit var prevPosition: Point
    lateinit var damage: IntArray

    fun decode(r: PacketReader, damagePerMob: Int) {
        mobId = r.readInteger()
        hitAction = r.readByte()
        val v37 = r.readByte()
        foreAction = v37.toInt() and 0x7F
        isLeft = v37.toInt() shr 7 and 1 != 0
        frameIdx = r.readByte()
        val v38 = r.readByte()
        calcDamageStatIndex = v38.toInt() and 0x7F
        isDoomed = v37.toInt() shr 7 and 1 != 0
        hitPosition = r.readPoint()
        prevPosition = r.readPoint()

        delay = r.readShort()
        damage = IntArray(damagePerMob)
        IntStream.range(0, damagePerMob).forEach { damage[it] = r.readInteger() }
        r.readInteger()
    }

    fun apply() {
        val mob = chr.field.getObject(FieldObjectType.MOB, mobId) as FieldMob? ?: return
        val totalDamage = Arrays.stream(damage).sum()

        mob.controller = chr
        mob.damage(chr, totalDamage)
    }
}