package net.maple.handlers.user

import client.Client
import net.maple.handlers.PacketHandler
import net.maple.packets.CharacterPackets.showDamage
import util.logging.LogType
import util.logging.Logger.log
import util.packet.PacketReader

class UserHitHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        val timestamp = reader.readInteger()
        val type = reader.readByte()
        val magicElemAttr = reader.readByte() // Element - 0x00 = elementless, 0x01 = ice, 0x02 = fire, 0x03 = lightning
        val dmg = reader.readInteger()

        when (type) {
            DamageType.MOB_PHYSICAL, DamageType.MOB_MAGIC -> {
                val mobId = reader.readInteger()
                val objId = reader.readInteger()
                val left = reader.readByte()
                val top = reader.readByte()
                val relativeDir = reader.readByte()
                val damageMissed = reader.readByte()
                val v284x = reader.readByte()

                chr.showDamage(type, dmg, mobId, left)
            }
            DamageType.OBSTACLE -> {
                chr.showDamage(type, dmg, 0, 0.toByte())
            }
            else -> {
                log(LogType.UNCODED, "Unknown damage type ($type)", this, c)
                return
            }
        }
        chr.modifyHealth(-dmg)
    }

    private object DamageType {
        // these nexon enums seem to be wrong
        //public static final byte MOB_PYSHICAL = 0x0;
        //public static final byte MOB_MAGIC = 0xFFFFFFFF;
        const val MOB_MAGIC: Byte = 0x00
        const val MOB_PHYSICAL: Byte = -0x01
        const val COUNTER: Byte = -0x02
        const val OBSTACLE: Byte = -0x03
        const val STAT: Byte = -0x04
    }
}