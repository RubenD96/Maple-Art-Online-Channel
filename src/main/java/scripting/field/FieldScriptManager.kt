package scripting.field

import scripting.ScriptManager

object FieldScriptManager : ScriptManager<String, FieldScript, Field>({ it.names.iterator() })