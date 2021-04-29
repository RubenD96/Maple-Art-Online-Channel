package scripting.dialog.npc

import scripting.ScriptManager

object NPCScriptManager : ScriptManager<Int, NPCScript, Npc>({ it.ids.iterator() })