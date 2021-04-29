package scripting

import constants.ServerConstants
import org.reflections.Reflections

abstract class ScriptManager<T, V : Script<T>, A : Annotation>(val keyGetter: (A) -> Iterator<T>) {

    val reflections = Reflections(ServerConstants.SCRIPTS_ROOT)
    val scripts: MutableMap<T, V> = HashMap()

    inline fun <reified C : A> loadScripts() {
        scripts.clear()

        val classes: Set<Class<*>> = reflections.getTypesAnnotatedWith(C::class.java)
        classes.forEach {
            it.getAnnotation(C::class.java).run {
                keyGetter(this)
            }.forEach { element ->
                val script = it.getConstructor().newInstance() as V
                script.value = element
                scripts[element] = script
            }
        }
    }

    operator fun get(key: T): V? {
        return scripts[key]
    }
}