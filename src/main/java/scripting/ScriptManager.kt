package scripting

import constants.ServerConstants
import org.reflections.Reflections

abstract class ScriptManager<T, V : Script> {

    val reflections = Reflections(ServerConstants.SCRIPTS_ROOT)
    val scripts: MutableMap<T, V> = HashMap()

    open fun loadScripts() {
        scripts.clear()
    }

    operator fun get(key: T): V? {
        return scripts[key]
    }
}