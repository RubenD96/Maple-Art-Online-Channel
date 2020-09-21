package net.maple.handlers.user

import client.Client
import client.player.quest.QuestRequest
import net.maple.handlers.PacketHandler
import scripting.quest.QuestScriptManager.converse
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
            converse(c, npcId, questId.toInt(), true)
        } else if (action.toInt() == QuestRequest.COMPLETE_SCRIPT.value) {
            val quest = c.character.quests[questId.toInt()]
            if (quest == null || !quest.canFinish()) {
                c.close(this, "Invalid quest finish requirements ($questId)")
                return
            }
            converse(c, npcId, questId.toInt(), false)
        } else if (action.toInt() == QuestRequest.RESIGN_QUEST.value) {
            c.character.forfeitQuest(questId.toInt())
        } else {
            println("Unknown/unhandled quest action ($action)")
            if (!c.isAdmin) {
                c.close(this, "Triggered unused quest action qid: $questId - action: $action")
            }
        }
    }
}