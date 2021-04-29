package scripting.dialog.quest

import scripting.ScriptManager

object QuestScriptManager : ScriptManager<Int, QuestScript>() {

    override fun loadScripts() {
        super.loadScripts()

        val quests: Set<Class<*>> = reflections.getTypesAnnotatedWith(Quest::class.java)
        quests.forEach {
            it.getAnnotation(Quest::class.java).ids.forEach { id ->
                val script = it.getConstructor().newInstance() as QuestScript
                script.id = id
                scripts[id] = script
            }
        }
    }
}