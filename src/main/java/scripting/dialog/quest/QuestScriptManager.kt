package scripting.dialog.quest

import scripting.ScriptManager

object QuestScriptManager : ScriptManager<Int, QuestScript, Quest>({ it.ids.iterator() })