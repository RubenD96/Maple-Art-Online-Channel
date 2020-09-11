package client.effects.user

import client.effects.AbstractEffect
import client.effects.EffectType
import util.packet.PacketWriter
import java.util.function.Consumer

class SkillUseEffect(val skillId: Int, val skillLevel: Byte) : AbstractEffect() {

    var additional: Consumer<PacketWriter>? = null

    override val type: EffectType get() = EffectType.SKILL_USE

    override fun encodeData(pw: PacketWriter) {
        pw.writeInt(skillId)
        pw.write(0)
        pw.write(skillLevel.toInt())
        if (additional != null) {
            additional?.accept(pw)
        }
    }
}