package scripting.scripts.quest.floor11

import client.Client
import client.player.quest.reward.ExpQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.bold
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([11008])
class TDSReleasingTheSoul11008 : QuestScript() {
    override fun execute(c: Client) {
        //No reason to implement.
    }

    override fun finish(c: Client) {
        execute(c, 2081008) {
            with(it) {
                sendMessage(
                    "(You hear the fluttering of wings coming from above...)",
                    next = { finalDialogue() }
                )
            }
        }
    }

    private fun DialogContext.finalDialogue() {
        postRewards(
            listOf(
                ExpQuestReward(250000)
            ),
            "Thank you human, my soul is now free." +
            "\r\nFarewell...".bold().blue()
        )
    }
}