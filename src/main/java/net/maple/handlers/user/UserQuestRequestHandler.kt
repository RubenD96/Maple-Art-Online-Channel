package net.maple.handlers.user

import client.Client
import client.player.quest.QuestRequest
import net.maple.handlers.PacketHandler
import net.maple.packets.ConversationPackets
import scripting.dialog.quest.QuestScriptManager
import util.HexTool.toHex
import util.packet.PacketReader

class UserQuestRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        println("[UserQuestRequestHandler] " + toHex(reader.data))

        val action = reader.readByte()
        val questId = reader.readShort()
        val npcId = reader.readInteger()
        val ptUserPosX = reader.readShort()
        val ptUserPosY = reader.readShort()

        if (action.toInt() == QuestRequest.OPENING_SCRIPT.value) {
            openQuest(c, questId.toInt(), npcId)
        } else if (action.toInt() == QuestRequest.COMPLETE_SCRIPT.value) {
            val quest = c.character.quests[questId.toInt()]
            if (quest == null || !quest.canFinish()) {
                c.close(this, "Invalid quest finish requirements ($questId)")
                return
            }
            openQuest(c, questId.toInt(), npcId, false)
        } else if (action.toInt() == QuestRequest.RESIGN_QUEST.value) {
            c.character.forfeitQuest(questId.toInt())
        } else {
            println("Unknown/unhandled quest action ($action)")
            if (!c.isAdmin) {
                c.close(this, "Triggered unused quest action qid: $questId - action: $action")
            }
        }
    }

    companion object {

        fun openQuest(c: Client, qid: Int, npc: Int, start: Boolean = true) {
            c.script = null
            QuestScriptManager[qid]?.let {
                if (start) {
                    it.execute(c)
                } else {
                    it.finish(c)
                }
            } ?: run {
                c.write(
                    ConversationPackets.getOkMessagePacket(npc, 0,
                    "This quest does not appear to have a script\r\n" +
                            "Please report this to a staff member\r\n" +
                            "QID: #r" + qid + "#k\r\n" +
                            "NPC: #r" + npc + "#k\r\n" +
                            "Map: #r" + c.character.fieldId))
            }
        }
    }
}