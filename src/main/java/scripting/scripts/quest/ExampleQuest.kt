package scripting.scripts.quest

import client.Client
import scripting.npc.DialogContext
import scripting.npc.DialogUtils
import scripting.npc.DialogUtils.blue
import scripting.quest.Quest
import scripting.quest.QuestScript

/**
 * For more examples regarding npc dialogues, check out ExampleNpc
 */
@Quest([3333])
class ExampleQuest : QuestScript() {

    /**
     * The basic check if a quest can be started is called in Character.startQuest()
     */
    override fun start(c: Client) {
        execute(c, 22000) {
            it.helloThere()
        }
    }

    /**
     * The basic check if a quest can be finished is called before this in UserQuestRequestHandler
     */
    override fun finish(c: Client) {
        execute(c, 22000) {
            it.goodJob()
        }
    }

    private fun DialogContext.helloThere() {
        sendMessage(
            "Hello there ${DialogUtils.playerName.blue()}!",
            ok = {
                startQuest()
                onEnd()
            }
        )
    }

    private fun DialogContext.goodJob() {
        sendMessage(
            "Thanks, good job :)",
            ok = {
                finishQuest()
                onEnd()
            }
        )
    }
}