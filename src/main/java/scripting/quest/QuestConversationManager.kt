package scripting.quest

import client.Client
import scripting.npc.NPCConversationManager

@Deprecated("Switched to stateless")
class QuestConversationManager(c: Client, npcId: Int, val id: Int, val isStart: Boolean) : NPCConversationManager(c, npcId) {

    override fun dispose() {
        QuestScriptManager.dispose(this, c)
    }

    fun startQuest() {
        super.startQuest(id)
    }

    fun completeQuest() {
        super.completeQuest(id)
    }

    fun reward(exp: Int, col: Int, items: Array<IntArray>) {
        if (exp != 0) gainExp(exp)
        if (col != 0) gainMeso(col)
        if (items.size > 1) {
            massGainItem(items)
        } else if (items.size == 1) {
            gainItem(items[0][0], items[0][1])
        }
    }
}