package client.inventory.item.templates

import client.inventory.item.flags.StatFlag
import util.packet.PacketReader

class StatChangeItemTemplate(id: Int, r: PacketReader) : ItemBundleTemplate(id, r) {

    val consumeFlags: Int
    var HP = 0
    var MP = 0
    var HPR = 0
    var MPR = 0
    var isNoCancelMouse = false
    var PAD: Short = 0
    var PDD: Short = 0
    var MAD: Short = 0
    var MDD: Short = 0
    var ACC: Short = 0
    var EVA: Short = 0
    var craft: Short = 0
    var speed: Short = 0
    var jump: Short = 0
    var morph: Short = 0
    var time = 0

    init {
        consumeFlags = r.readInteger()
        if (containsFlag(StatFlag.HP)) HP = r.readInteger()
        if (containsFlag(StatFlag.MP)) MP = r.readInteger()
        if (containsFlag(StatFlag.HPR)) HPR = r.readInteger()
        if (containsFlag(StatFlag.MPR)) MPR = r.readInteger()
        if (containsFlag(StatFlag.NO_CANCEL_MOUSE)) isNoCancelMouse = r.readBool()
        if (containsFlag(StatFlag.PAD)) PAD = r.readShort()
        if (containsFlag(StatFlag.PDD)) PDD = r.readShort()
        if (containsFlag(StatFlag.MAD)) MAD = r.readShort()
        if (containsFlag(StatFlag.MDD)) MDD = r.readShort()
        if (containsFlag(StatFlag.ACC)) ACC = r.readShort()
        if (containsFlag(StatFlag.EVA)) EVA = r.readShort()
        if (containsFlag(StatFlag.CRAFT)) craft = r.readShort()
        if (containsFlag(StatFlag.SPEED)) speed = r.readShort()
        if (containsFlag(StatFlag.JUMP)) jump = r.readShort()
        if (containsFlag(StatFlag.MORPH)) morph = r.readShort()
        if (containsFlag(StatFlag.TIME)) time = r.readInteger()
    }

    fun containsFlag(flag: StatFlag): Boolean {
        return consumeFlags and flag.value == flag.value
    }

    override fun toString(): String {
        return "StatChangeItemTemplate(consumeFlags=$consumeFlags, hP=$HP, mP=$MP, hPR=$HPR, mPR=$MPR, isNoCancelMouse=$isNoCancelMouse, pAD=$PAD, pDD=$PDD, mAD=$MAD, mDD=$MDD, aCC=$ACC, eVA=$EVA, craft=$craft, speed=$speed, jump=$jump, morph=$morph, time=$time)"
    }
}