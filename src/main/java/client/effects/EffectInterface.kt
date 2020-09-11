package client.effects

import util.packet.PacketWriter

interface EffectInterface {
    val type: EffectType
    fun encode(pw: PacketWriter)
}