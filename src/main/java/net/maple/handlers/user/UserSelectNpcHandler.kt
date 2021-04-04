package net.maple.handlers.user

import client.Client
import field.obj.life.FieldNPC
import managers.NPCShopManager.getShop
import net.maple.handlers.PacketHandler
import net.maple.packets.ConversationPackets
import net.server.Server.shops
import scripting.ScriptManager
import scripting.npc.NPCScriptManager.converse
import util.packet.PacketReader

class UserSelectNpcHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character
        val npcObjectId = reader.readInteger()

        val npc: FieldNPC = chr.field.getObjects<FieldNPC>().stream()
                .filter { it.id == npcObjectId }
                .findFirst().orElse(null)
                ?: return c.close(this, "Clicked un an non-existent npc noid: $npcObjectId mapid: ${chr.fieldId}")

        println("[UserSelectNpcHandler] ${npc.name} (${npc.npcId})")
        openNpc(c, npc)
    }

    companion object {

        fun openNpc(c: Client, npc: FieldNPC) {
            ScriptManager.npcScripts[npc.npcId]?.let {
                it.start(c)
            } ?: run {
                if (shops.contains(npc.npcId)) {
                    getShop(npc.npcId).open(c.character)
                } else {
                    c.write(ConversationPackets.getOkMessagePacket(npc.npcId, 0,
                        "This npc does not appear to have a script\r\n" +
                                "Please report this to a staff member\r\n" +
                                "ID: #r" + npc.npcId + "#k\r\n" +
                                "Map: #r" + c.character.fieldId))
                }
            }
            /*val hasNpcScript = converse(c, npc.npcId)
            if (!hasNpcScript) {
                if (shops.contains(npc.npcId)) {
                    getShop(npc.npcId).open(c.character)
                } else {
                    c.write(ConversationPackets.getOkMessagePacket(npc.npcId, 0,
                        "This npc does not appear to have a script\r\n" +
                                "Please report this to a staff member\r\n" +
                                "ID: #r" + npc.npcId + "#k\r\n" +
                                "Map: #r" + c.character.fieldId))
                }
            }*/
        }
    }
}