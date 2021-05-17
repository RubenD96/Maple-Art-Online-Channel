package client.effects.user

import client.effects.AbstractEffect
import client.effects.EffectType
import util.packet.PacketWriter

class PetEffect(val option: Byte, val pet: Byte) : AbstractEffect() {

    override val type = EffectType.PET

    override fun encodeData(pw: PacketWriter) {
        pw.writeByte(option)
        pw.writeByte(pet)
    }
}