package scripting.mob

import scripting.ScriptManager

object MobScriptManager : ScriptManager<Int, MobScript, Mob>({ it.ids.iterator() })