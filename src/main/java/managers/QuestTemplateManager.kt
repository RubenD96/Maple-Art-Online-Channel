package managers

import client.player.quest.QuestTemplate

object QuestTemplateManager : AbstractManager() {

    private val templates: MutableMap<Int, QuestTemplate> = HashMap()

    fun getQuest(id: Int): QuestTemplate? {
        var template = templates[id]
        if (template == null) {
            template = QuestTemplate(id)
            if (!loadQuestData(template)) return null
            templates[id] = template
        }
        return template
    }

    private fun loadQuestData(template: QuestTemplate): Boolean {
        val r = getData("wz/Quest/" + template.id + ".mao") ?: return false

        val sflags = r.readInteger()
        val eflags = r.readInteger()
        template.startingRequirements.decode(sflags, r)
        template.endingRequirements.decode(eflags, r)

        //System.out.println("Finished initializing quest: " + template.getId());
        return true
    }
}