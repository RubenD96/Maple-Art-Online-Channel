package client.effects

import util.packet.PacketWriter

abstract class AbstractFieldEffect : FieldEffectInterface {

    override fun encode(pw: PacketWriter) {
        pw.write(type.value)
        encodeData(pw)
    }

    protected abstract fun encodeData(pw: PacketWriter)
}