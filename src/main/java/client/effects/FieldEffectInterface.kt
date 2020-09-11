package client.effects

import util.packet.PacketWriter

interface FieldEffectInterface {
    val type: FieldEffectType
    fun encode(pw: PacketWriter)
}