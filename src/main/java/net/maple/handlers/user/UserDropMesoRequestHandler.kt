package net.maple.handlers.user

import client.Client
import field.obj.drop.MesoDrop
import net.maple.handlers.PacketHandler
import util.packet.PacketReader

class UserDropMesoRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        reader.readInteger() // timestamp
        val meso = reader.readInteger()

        if (meso <= chr.meso && meso > 9 && meso < 50001) {
            chr.gainMeso(-meso)
            val drop = MesoDrop(chr.id, chr, meso, 0)
            drop.field = chr.field
            drop.position = chr.position
            chr.field.enter(drop)
        } else {
            c.close(this, "Invalid meso drop amount ($meso)")
        }
    }
}