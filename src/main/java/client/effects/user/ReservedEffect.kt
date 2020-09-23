package client.effects.user

import client.effects.AbstractEffect
import client.effects.EffectType
import util.packet.PacketWriter

class ReservedEffect(val path: String) : AbstractEffect() {

    override val type = EffectType.RESERVED_EFFECT

    override fun encodeData(pw: PacketWriter) {
        pw.writeMapleString(path)
    }
}