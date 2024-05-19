package client.stats

import client.inventory.item.templates.StatChangeItemTemplate
import skill.SkillLevelTemplate
import util.packet.PacketWriter
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs

object TemporaryStatExtensions {

    fun SkillLevelTemplate.getTemporaryStats(): Map<TemporaryStatType, Short> {
        val stats = mutableMapOf<TemporaryStatType, Short>()

        if (pad != 0.toShort()) stats[TemporaryStatType.PAD] = pad
        if (pdd != 0.toShort()) stats[TemporaryStatType.PDD] = pdd
        if (mad != 0.toShort()) stats[TemporaryStatType.MAD] = mad
        if (mdd != 0.toShort()) stats[TemporaryStatType.MDD] = mdd
        if (acc != 0.toShort()) stats[TemporaryStatType.ACC] = acc
        if (eva != 0.toShort()) stats[TemporaryStatType.EVA] = eva
        if (craft != 0.toShort()) stats[TemporaryStatType.CRAFT] = craft
        if (speed != 0.toShort()) stats[TemporaryStatType.SPEED] = speed
        if (jump != 0.toShort()) stats[TemporaryStatType.JUMP] = jump

        if (morph > 0) stats[TemporaryStatType.MORPH] = morph

        if (emhp != 0.toShort()) stats[TemporaryStatType.EMHP] = emhp
        if (emmp != 0.toShort()) stats[TemporaryStatType.EMMP] = emmp
        if (epad != 0.toShort()) stats[TemporaryStatType.EPAD] = epad
        if (epdd != 0.toShort()) stats[TemporaryStatType.EPDD] = epdd

        when (Skill.entries[skill]) {
            Skill.MAGICIAN_MAGIC_GUARD, Skill.FLAMEWIZARD_MAGIC_GUARD, Skill.EVAN_MAGIC_GUARD -> {
                stats[TemporaryStatType.MAGIC_GUARD] = x
            }
            Skill.ROGUE_DARK_SIGHT, Skill.NIGHTWALKER_DARK_SIGHT -> {
                stats[TemporaryStatType.DARK_SIGHT] = x
            }

            else -> println("$skill not handled yet")
        }

        return stats
    }

    fun StatChangeItemTemplate.getTemporaryStats(): Map<TemporaryStatType, Short> {
        val stats = HashMap<TemporaryStatType, Short>()

        if (PAD.toInt() != 0) stats[TemporaryStatType.PAD] = PAD
        if (PDD.toInt() != 0) stats[TemporaryStatType.PDD] = PDD
        if (MAD.toInt() != 0) stats[TemporaryStatType.MAD] = MAD
        if (MDD.toInt() != 0) stats[TemporaryStatType.MDD] = MDD
        if (ACC.toInt() != 0) stats[TemporaryStatType.ACC] = ACC
        if (EVA.toInt() != 0) stats[TemporaryStatType.EVA] = EVA
        if (craft.toInt() != 0) stats[TemporaryStatType.CRAFT] = craft
        if (speed.toInt() != 0) stats[TemporaryStatType.SPEED] = speed
        if (jump.toInt() != 0) stats[TemporaryStatType.JUMP] = jump
        if (morph > 0) stats[TemporaryStatType.MORPH] = morph

        return stats
    }

    fun Map<TemporaryStatType, TemporaryStat>.encodeMask(pw: PacketWriter) {
        val bits = BitSet(128)

        keys.forEach {
            //bits[it.type] = true
            bits.set(it.type)
        }

        val bytes = bits.toByteArray()
        bytes.reverse()
        repeat(4) {
            try {
                pw.writeInt(abs(bytes[it].toInt()))
            } catch (_: Exception) {
                pw.writeInt(0)
            }
        }
        /*for (i in 3 downTo 0) {
            try {
                pw.writeInt(abs(bytes[i].toInt()))
            } catch (_: Exception) {
                pw.writeInt(0)
            }
        }*/
    }

    fun Map<TemporaryStatType, TemporaryStat>.encodeLocal(pw: PacketWriter) {
        encodeMask(pw)

        val now = System.currentTimeMillis()

        TemporaryStatOrder.encodingOrderLocal.forEach { type ->
            this[type]?.let {
                pw.writeShort(it.option)
                pw.writeInt(it.templateId)
                pw.writeInt(if (it.expire != 0L) (it.expire - now).toInt() else Int.MAX_VALUE)
            }
        }

        pw.writeBool(false) // bDefenseAtt
        pw.writeBool(false) // bDefenseState

        if (containsKey(TemporaryStatType.SWALLOW_ATTACK_DAMAGE) &&
            containsKey(TemporaryStatType.SWALLOW_DEFENCE) &&
            containsKey(TemporaryStatType.SWALLOW_CRITICAL) &&
            containsKey(TemporaryStatType.SWALLOW_MAX_HP) &&
            containsKey(TemporaryStatType.SWALLOW_EVASION)
        ) {
            pw.write(0)
        }

        if (containsKey(TemporaryStatType.DICE)) {
            repeat(22) {
                pw.writeInt(0)
            }
        }

        if (containsKey(TemporaryStatType.BLESSING_ARMOR)) pw.writeInt(0)

        encodeTwoState(pw)
    }

    fun Map<TemporaryStatType, TemporaryStat>.encodeRemote(pw: PacketWriter) {
        encodeMask(pw)

        TemporaryStatOrder.encodingOrderRemote.forEach {
            this[it.key]?.let { ts ->
                it.value.accept(Pair(ts, pw))
            }
        }

        pw.write(0) // nDefenseAtt
        pw.write(0) // nDefenseState

        encodeTwoState(pw)
    }

    private fun Map<TemporaryStatType, TemporaryStat>.encodeTwoState(pw: PacketWriter) {
        val now = System.currentTimeMillis()

        TemporaryStatOrder.encodingTwoStateOrderRemote.forEach { type ->
            this[type]?.let {
                pw.writeInt(it.option)
                pw.writeInt(it.templateId)

                if (it.expire != 0L) {
                    pw.writeBool(now > it.expire)
                    pw.writeInt((now - it.expire).toInt() / 1000)
                } else {
                    pw.writeBool(true)
                    pw.writeInt(Int.MAX_VALUE)
                }
            }
        }
    }
}