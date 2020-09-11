package client.effects

import util.packet.PacketWriter

class Effect(override val type: EffectType) : AbstractEffect() {

    override fun encodeData(pw: PacketWriter) {}
}