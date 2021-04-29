package scripting.portal

import scripting.ScriptManager

object PortalScriptManager : ScriptManager<String, PortalScript, Portal>({ it.names.iterator() })