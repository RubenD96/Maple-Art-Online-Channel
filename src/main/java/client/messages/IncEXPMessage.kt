package client.messages

import util.packet.PacketWriter

class IncEXPMessage : AbstractMessage() {

    var isLastHit = false
    var onQuest = false
    var exp = 0
    var expBySMQ = 0
    var weddingBonusExp = 0
    var partyBonusExp = 0
    var itemBonusExp = 0
    var premiumIPExp = 0
    var rainbowWeekEventExp = 0
    var partyExpRingExp = 0
    var cakePieEventBonus = 0
    var mobEventBonusPercentage: Byte = 0
    var partyBonusPercentage: Byte = 0
    var playTimeHour: Byte = 0
    var questBonusRate: Byte = 0
    var questBonusRemainCount: Byte = 0
    var partyBonusEventRate: Byte = 0

    override val type: MessageType get() = MessageType.INC_EXP_MESSAGE

    override fun encodeData(pw: PacketWriter) {
        pw.writeBool(isLastHit)
        pw.writeInt(exp)
        pw.writeBool(onQuest)
        pw.writeInt(expBySMQ)
        pw.write(mobEventBonusPercentage.toInt())
        pw.write(partyBonusPercentage.toInt())
        pw.writeInt(weddingBonusExp)

        if (mobEventBonusPercentage > 0) {
            pw.write(playTimeHour.toInt())
        }

        if (onQuest) {
            pw.write(questBonusRate.toInt())
            if (questBonusRate > 0) {
                pw.write(questBonusRemainCount.toInt())
            }
        }

        pw.writeByte(partyBonusEventRate)
        pw.writeInt(partyBonusExp)
        pw.writeInt(itemBonusExp)
        pw.writeInt(premiumIPExp)
        pw.writeInt(rainbowWeekEventExp)
        pw.writeInt(partyExpRingExp)
        pw.writeInt(cakePieEventBonus)
    }
}