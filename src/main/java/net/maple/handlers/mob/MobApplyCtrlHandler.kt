package net.maple.handlers.mob

import client.Client
import field.obj.FieldObjectType
import field.obj.life.FieldMob
import net.maple.handlers.PacketHandler
import util.packet.PacketReader

class MobApplyCtrlHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val oid = reader.readInteger()
        val distanceToPlayer = reader.readInteger()

        val chr = c.character
        val field = chr.field
        val mob = field.getObject(FieldObjectType.MOB, oid) as FieldMob? ?: return

        //println("[MobApplyCtrlHandler] (${chr.getName()}) ${mob.name} ($oid) distance: $distanceToPlayer");
        when {
            mob.controller == null -> {
                mob.controllerDistance = distanceToPlayer
                mob.controller = chr
                field.updateControlledObjects()
            }
            mob.controller == chr -> {
                mob.controllerDistance = distanceToPlayer
            }
            distanceToPlayer < mob.controllerDistance -> {
                mob.controllerDistance = distanceToPlayer
                mob.controller = chr
                field.updateControlledObjects()
            }
        }
    }
}