package net.maple.handlers.user

import client.Client
import net.maple.handlers.PacketHandler
import scripting.npc.ConversationType
import scripting.npc.NPCScriptManager
import scripting.npc.NPCScriptManager.cms
import scripting.quest.QuestConversationManager
import scripting.quest.QuestScriptManager
import scripting.quest.QuestScriptManager.qms
import util.HexTool.toHex
import util.packet.PacketReader

class UserScriptMessageAnswerHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        println("[UserScriptMessageAnswerHandler] " + toHex(reader.data))

        val type = reader.readByte()
        val action = reader.readByte() // 1 = continue, 255 = end chat

        var cm = cms[c]
        if (cm == null) {
            cm = qms[c]
        }

        if (cm != null) {
            var selection = -1
            if (type.toInt() == ConversationType.ASK_MENU.value || type.toInt() == ConversationType.ASK_NUMBER.value) {
                selection = reader.readInteger()
            }

            if ((type.toInt() == ConversationType.ASK_TEXT.value || type.toInt() == ConversationType.ASK_BOX_TEXT.value) && action.toInt() == 1) {
                cm.text = reader.readMapleString()
            }

            if (cm is QuestConversationManager) {
                QuestScriptManager.converse(c, action.toInt(), selection)
            } else {
                NPCScriptManager.converse(c, action.toInt(), selection)
            }
        }
    }
}