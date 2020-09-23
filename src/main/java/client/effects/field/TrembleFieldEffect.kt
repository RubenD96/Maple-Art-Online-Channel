package client.effects.field

import client.effects.AbstractFieldEffect
import client.effects.FieldEffectType
import util.packet.PacketWriter

class TrembleFieldEffect(private val heavy: Boolean, val delay: Int) : AbstractFieldEffect() {

    override val type = FieldEffectType.TREMBLE

    override fun encodeData(pw: PacketWriter) {
        pw.writeBool(!heavy)
        pw.writeInt(delay * 1000) // milliseconds
    }
}