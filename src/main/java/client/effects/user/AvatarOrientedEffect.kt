package client.effects.user

import client.effects.AbstractEffect
import client.effects.EffectType
import util.packet.PacketWriter

class AvatarOrientedEffect(val path: String) : AbstractEffect() {

    override val type = EffectType.AVATAR_ORIENTED

    override fun encodeData(pw: PacketWriter) {
        pw.writeMapleString(path)
        pw.writeInt(0)
    }
}