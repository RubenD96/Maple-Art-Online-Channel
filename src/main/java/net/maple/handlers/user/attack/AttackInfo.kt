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
    var actionAndDir: Short = 0
    var isLeft = false
    var damageInfo: MutableList<DamageInfo> = ArrayList()

    fun decode() {
        r.readInteger() // pDrInfo.dr0
        r.readInteger() // pDrInfo.dr1

        val v6 = r.readByte().toInt()
        damagePerMob = v6 and 0x0F
        mobCount = v6 shr 4/* and 0x0F rebirth src*/

        r.readInteger() // pDrInfo.dr2
        r.readInteger() // pDrInfo.dr3

        skillId = r.readInteger()
        /* probably more id's since v53
        if ( nSkillId == 2121001 || nSkillId == 2221001 || nSkillId == 2321001 || nSkillId == 3221001 || nSkillId == 3121004 )
            v142 = CInPacket::Decode4(v5);
         */
        r.readByte() // HIBYTE(v732[5]._ZtlSecureTear_nLUK[1])

        if (type == AttackType.MAGIC) {
            repeat(6) {
                r.readInteger()
            }
        }

        /*
            n = get_rand(pDrInfo.dr0, 0);
            COutPacket::Encode4(&oPacket, n);
            v383[1] = 0;
            v245 = CCrc32::GetCrc32(pData, 4u, n, 0, 0);
            COutPacket::Encode4(&oPacket, v245);
         */
        val rand = r.readInteger()
        if (rand == chr.prevRand) {
            chr.client.close(this, "Same consecutive rand in dmg check")
            return
        }
        val crc = r.readInteger()

        r.readInteger()
        r.readInteger()
        // r.readInteger(); // keydown
        option = r.readByte()

        if (type == AttackType.SHOOT) {
            r.readByte()
        }

        actionAndDir = r.readShort()
        isLeft = (actionAndDir.toInt() shr 15) and 1 != 0
        action = actionAndDir.toInt() and 0x7FFF

        r.readInteger() // crc

        attackActionType = r.readByte()
        attackSpeed = r.readByte()
        attackTime = r.readInteger()

        r.readInteger() // bmage?

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