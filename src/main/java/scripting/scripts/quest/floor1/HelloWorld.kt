package scripting.scripts.quest.floor1

import client.Client
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([1])
class HelloWorld : QuestScript() {

    override fun execute(c: Client) {
        execute(c,22000) {
            it.sendMessage("Hello World!", next = { it.startQuest() })
        }
    }

    override fun finish(c: Client) {
        execute(c,22000) {
            it.sendMessage("Bye World!", next = { it.finishQuest() })
        }
    }
}