package scripting

import constants.ServerConstants
import org.reflections.Reflections
import scripting.npc.NPCScript
import scripting.npc.Npc
import scripting.quest.Quest
import scripting.quest.QuestScript

object ScriptManager {

    val npcScripts: MutableMap<Int, NPCScript> = HashMap()
    val questScripts: MutableMap<Int, QuestScript> = HashMap()

    fun loadScripts() {
        npcScripts.clear()
        val reflections = Reflections(ServerConstants.SCRIPTS_ROOT)

        // npc's
        val npcs: Set<Class<*>> = reflections.getTypesAnnotatedWith(Npc::class.java)
        npcs.forEach {
            it.getAnnotation(Npc::class.java).ids.forEach { id ->
                val script = it.getConstructor().newInstance() as NPCScript
                script.id = id
                npcScripts[id] = script
            }
        }

        // quests
        val quests: Set<Class<*>> = reflections.getTypesAnnotatedWith(Quest::class.java)
        quests.forEach {
            it.getAnnotation(Quest::class.java).ids.forEach { id ->
                val script = it.getConstructor().newInstance() as QuestScript
                script.id = id
                questScripts[id] = script
            }
        }
    }
}