package scripting.scripts.quest.floor10

import client.Client
import client.player.quest.reward.ExpQuestReward
import client.player.quest.reward.MesoQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.red
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([1010])
class UnluckyProblems1010 : QuestScript() {

    override fun execute(c: Client) {
        execute(c, 2071007) {
            with(it) {
                sendMessage(
                    "We have a huge rabbit infestation." +
                    "\r\nThey are invading our crops and eating every crop in our village." +
                            "\r\nDo you think you could handle it for me?",
                    accept = { onAccept() },
                    decline = { onDecline() }

                )
            }
        }
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "Please let me know when you have reconsidered.",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "Please eliminate ${"75 White Desert Rabbits".red()} and ${"75 Brown Desert Rabbits".red()} for us.",
            ok = { onEnd() }
        )
    }


    override fun finish(c: Client) {
        execute(c, 2071007) {
            with(it) {
                postRewards(
                    listOf(
                        ExpQuestReward(5000),
                        MesoQuestReward(10000)
                    ),
                    "Thank you for dealing with the infestation! \n\nHere is your reward.",
                )
            }
        }
    }
}