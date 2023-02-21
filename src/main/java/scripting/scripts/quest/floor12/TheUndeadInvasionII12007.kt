package scripting.scripts.quest.floor12

import client.Client
import client.player.quest.reward.ExpQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([12007])
class TheUndeadInvasionII12007: QuestScript() {
    override fun DialogContext.onEnd() {
        endMessage("But I need more time!")
    }

    override fun execute(c: Client) {
        execute(c, 9201096) {
            with(it) {
                sendMessage(
                    "Thanks for stalling for this long, but we're not ready!" +
                    "\r\n\r\nMe and Brandon are currently performing research on some of the body parts of the fish to figure out an efficient way to combat them." +
                    "\r\nWe did not yet find a solution, but I'm sure we will, we are so close!",
                    next = { questionToStartQuest() }
                )
            }
        }
    }

    //Maybe create an exit path to this quest with less rewards!
    private fun DialogContext.questionToStartQuest() {
        sendMessage(
            "Can you hold for just a bit longer?",
            accept = { onAccept() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage("Thank you!")
    }

    override fun finish(c: Client) {
        execute(c, 9201096) {
            with(it) {
                postRewards(
                    listOf(
                        ExpQuestReward(90000)
                    ),
                    "~As you step out of the cave, you hear shouts echo throughout the outside of the cave~" +
                    "\r\nYes! this should work!"
                )
            }
        }
    }
}