package scripting.quest

import client.Client
import scripting.AbstractScriptManager
import javax.script.Invocable

object QuestScriptManager : AbstractScriptManager() {

    val qms: MutableMap<Client, QuestConversationManager> = HashMap()
    private val scripts: MutableMap<Client, Invocable> = HashMap()

    fun converse(c: Client, npc: Int, questId: Int, start: Boolean) {
        converse(c, npc, questId, start, 1, 0)
    }

    fun converse(c: Client, mode: Int, selection: Int) {
        val qm = qms[c] ?: return
        converse(c, qm.npcId, qm.id, qm.isStart, mode, selection)
    }

    fun converse(c: Client, npc: Int, questId: Int, start: Boolean, mode: Int, selection: Int): Boolean {
        //MapleQuest quest = MapleQuest.getInstance(questid);
        /*if (!c.getPlayer().getQuest(quest).getStatus().equals(MapleQuestStatus.Status.NOT_STARTED)) {
            dispose(c);
            return;
        }*/
        try {
            if (scripts[c] == null) {
                val qm = QuestConversationManager(c, npc, questId, start)
                if (qms.containsKey(c)) {
                    dispose(c)
                }
                if (c.canClickNPC()) {
                    qms[c] = qm
                    val iv = getInvocable("quest/$questId.js", c)

                    if (iv == null) {
                        println("Quest $questId is uncoded.")
                        return false
                    }

                    val unmutableEngine = engine ?: return false
                    unmutableEngine.put("cm", qm)
                    scripts[c] = iv

                    c.lastNpcClick = System.currentTimeMillis()
                    iv.invokeFunction(if (start) "start" else "end", mode, selection)
                } else {
                    c.character.enableActions()
                }
            } else {
                c.lastNpcClick = System.currentTimeMillis()
                scripts[c]!!.invokeFunction(if (start) "start" else "end", mode, selection)
            }
        } catch (e: Exception) {
            //System.err.println(e.getMessage());
            e.printStackTrace()
            dispose(c)
            return false
        }
        return true
    }

    fun dispose(qm: QuestConversationManager, c: Client) {
        qms.remove(c)
        scripts.remove(c)
        resetContext("quest/" + qm.id + ".js", c)
    }

    fun dispose(c: Client) {
        val qm = qms[c] ?: return
        dispose(qm, c)
    }
}