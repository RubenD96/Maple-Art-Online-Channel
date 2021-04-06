package scripting

import constants.ServerConstants
import org.reflections.Reflections
import scripting.npc.NPCScript
import scripting.npc.Npc

object ScriptManager {

    val npcScripts: MutableMap<Int, NPCScript> = HashMap()

    fun loadScripts() {
        npcScripts.clear()
        val reflections = Reflections(ServerConstants.SCRIPTS_ROOT)
        val annotated: Set<Class<*>> = reflections.getTypesAnnotatedWith(Npc::class.java)
        annotated.forEach {
            it.getAnnotation(Npc::class.java).ids.forEach { id ->
                val script = it.getConstructor().newInstance() as NPCScript
                script.id = id
                npcScripts[id] = script
            }
        }
    }
}