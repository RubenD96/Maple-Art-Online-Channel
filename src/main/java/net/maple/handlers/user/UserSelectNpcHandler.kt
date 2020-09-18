package net.maple.handlers.user

import client.Client
import field.obj.FieldObject
import field.obj.FieldObjectType
import field.obj.life.FieldNPC
import managers.NPCShopManager.getShop
import net.maple.handlers.PacketHandler
import net.maple.packets.ConversationPackets
import net.server.Server.shops
import scripting.npc.NPCScriptManager.converse
import util.packet.PacketReader

class UserSelectNpcHandler : PacketHandler() {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character
        val npcObjectId = reader.readInteger()

        val npc: FieldNPC = chr.field.getObjects(FieldObjectType.NPC)
                .stream().filter { o: FieldObject -> o.id == npcObjectId }
                .findFirst().orElse(null) as FieldNPC? ?: return

        println("[UserSelectNpcHandler] ${npc.name} (${npc.npcId})")
        val hasNpcScript = converse(c, npc.npcId)
        if (!hasNpcScript) {
            if (shops.contains(npc.npcId)) {
                getShop(npc.npcId).open(chr)
            } else {
                c.write(ConversationPackets.getOkMessagePacket(npc.npcId, 0,
                        "This npc does not appear to have a script\r\n" +
                                "Please report this to a staff member\r\n" +
                                "ID: #r" + npc.npcId + "#k\r\n" +
                                "Map: #r" + chr.fieldId))
            }
        }
    }
}