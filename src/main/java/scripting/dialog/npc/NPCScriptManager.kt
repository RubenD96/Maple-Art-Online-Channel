package scripting.dialog.npc

import scripting.ScriptManager

object NPCScriptManager : ScriptManager<Int, NPCScript>() {

    override fun loadScripts() {
        super.loadScripts()

        val npcs: Set<Class<*>> = reflections.getTypesAnnotatedWith(Npc::class.java)
        npcs.forEach {
            it.getAnnotation(Npc::class.java).ids.forEach { id ->
                val script = it.getConstructor().newInstance() as NPCScript
                script.id = id
                scripts[id] = script
            }
        }
    }
}