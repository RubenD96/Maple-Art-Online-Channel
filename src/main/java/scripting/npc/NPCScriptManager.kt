package scripting.npc

import client.Client
import scripting.AbstractScriptManager
import javax.script.Invocable

@Deprecated("Switched to stateless")
object NPCScriptManager : AbstractScriptManager() {

    val cms: MutableMap<Client, NPCConversationManager> = HashMap()
    private val scripts: MutableMap<Client, Invocable> = HashMap()

    fun converse(c: Client, mode: Int, selection: Int) {
        val cm = cms[c] ?: return
        converse(c, cm.npcId, null, mode, selection)
    }

    fun converse(c: Client, npc: Int, fileName: String? = null, mode: Int = 1, selection: Int = -1): Boolean {
        return try {
            if (c.canClickNPC()) {
                c.lastNpcClick = System.currentTimeMillis()
                if (scripts[c] == null) {
                    val cm = NPCConversationManager(c, npc)

                    if (cms.containsKey(c)) {
                        dispose(c)
                    }

                    cms[c] = cm

                    var iv: Invocable? = null
                    if (fileName != null) {
                        iv = getInvocable("npc/$fileName.js", c)
                    }
                    if (iv == null) {
                        iv = getInvocable("npc/$npc.js", c)
                    }
                    if (iv == null) {
                        dispose(c)
                        return false
                    }

                    val unmutableEngine = engine ?: return false
                    unmutableEngine.put("cm", cm)
                    scripts[c] = iv
                    try {
                        iv.invokeFunction("init")
                    } catch (ignored: NoSuchMethodException) {
                    } finally {
                        iv.invokeFunction("converse", mode, selection)
                    }
                } else {
                    scripts[c]?.invokeFunction("converse", mode, selection)
                }
            } else {
                c.character.enableActions()
            }
            true
        } catch (e: Exception) {
            //System.err.println(e.getMessage());
            e.printStackTrace()
            dispose(c)
            false
        }
    }

    fun dispose(cm: NPCConversationManager) {
        val c = cm.c
        cms.remove(c)
        scripts.remove(c)
        resetContext("npc/" + cm.npcId + ".js", c)
    }

    fun dispose(c: Client) {
        val cm = cms[c] ?: return
        dispose(cm)
    }
}