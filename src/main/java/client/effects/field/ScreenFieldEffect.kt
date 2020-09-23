package client.effects.field

import client.effects.AbstractFieldEffect
import client.effects.FieldEffectType
import util.packet.PacketWriter

class ScreenFieldEffect(val path: String) : AbstractFieldEffect() {

    override val type = FieldEffectType.SCREEN

    override fun encodeData(pw: PacketWriter) {
        pw.writeMapleString(path)
    }
}