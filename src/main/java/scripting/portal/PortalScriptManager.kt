package scripting.portal

import scripting.ScriptManager
import util.logging.LogType
import util.logging.Logger

object PortalScriptManager : ScriptManager<String, PortalScript>() {

    override fun loadScripts() {
        super.loadScripts()

        val quests: Set<Class<*>> = reflections.getTypesAnnotatedWith(Portal::class.java)
        quests.forEach {
            it.getAnnotation(Portal::class.java).names.forEach { name ->
                val script = it.getConstructor().newInstance() as PortalScript
                script.name = name
                scripts[name] = script
            }
        }
    }

    fun portalError(name: String) {
        Logger.log(LogType.MISSING, "Missing proper portal script $name", this)
    }
}