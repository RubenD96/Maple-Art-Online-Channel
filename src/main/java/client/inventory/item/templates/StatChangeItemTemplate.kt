package client.inventory.item.templates

import client.inventory.item.flags.StatFlag
import util.packet.PacketReader

class StatChangeItemTemplate(id: Int, r: PacketReader) : ItemBundleTemplate(id, r) {

    val consumeFlags: Int
    var hP = 0
    var mP = 0
    var hPR = 0
    var mPR = 0
    var isNoCancelMouse = false
    var pAD: Short = 0
    var pDD: Short = 0
    var mAD: Short = 0
    var mDD: Short = 0
    var aCC: Short = 0
    var eVA: Short = 0
    var craft: Short = 0
    var speed: Short = 0
    var jump: Short = 0
    var morph: Short = 0
    var time = 0

    init {
        consumeFlags = r.readInteger()
        if (containsFlag(StatFlag.HP)) hP = r.readInteger()
        if (containsFlag(StatFlag.MP)) mP = r.readInteger()
        if (containsFlag(StatFlag.HPR)) hPR = r.readInteger()
        if (containsFlag(StatFlag.MPR)) mPR = r.readInteger()
        if (containsFlag(StatFlag.NO_CANCEL_MOUSE)) isNoCancelMouse = r.readBool()
        if (containsFlag(StatFlag.PAD)) pAD = r.readShort()
        if (containsFlag(StatFlag.PDD)) pDD = r.readShort()
        if (containsFlag(StatFlag.MAD)) mAD = r.readShort()
        if (containsFlag(StatFlag.MDD)) mDD = r.readShort()
        if (containsFlag(StatFlag.ACC)) aCC = r.readShort()
        if (containsFlag(StatFlag.EVA)) eVA = r.readShort()
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
        return "StatChangeItemTemplate(consumeFlags=$consumeFlags, hP=$hP, mP=$mP, hPR=$hPR, mPR=$mPR, isNoCancelMouse=$isNoCancelMouse, pAD=$pAD, pDD=$pDD, mAD=$mAD, mDD=$mDD, aCC=$aCC, eVA=$eVA, craft=$craft, speed=$speed, jump=$jump, morph=$morph, time=$time)"
    }
}