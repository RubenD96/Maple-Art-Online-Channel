package scripting.scripts.quest.floor1

import client.Client
import client.player.quest.reward.ExpQuestReward
import client.player.quest.reward.MesoQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([1011])
class RunningLowOnWood1011 : QuestScript() {

    override fun execute(c: Client) {
        execute(c, 2071002) {
            with(it) {
                sendMessage(
                    "We are running out of wood to expand our village." +
                            "\r\nCould you help us gather some?",
                    accept = { onAccept() },
                    decline = { onDecline() }

                )
            }
        }
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "We really need help in gathering these resources.",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "Please bring me ${"50 Firewood".blue()} and ${"50 Seedlings".blue()}.",
            ok = { onEnd() }
        )
    }


    override fun finish(c: Client) {
        execute(c, 2071002) {
            with(it) {
                postRewards(
                    listOf(
                        ExpQuestReward(5000),
                        MesoQuestReward(10000)
                    ),
                    "Our village is eternally grateful!",
                take = mapOf(
                    4000018 to 50,
                    4000195 to 50)
                )
            }
        }
    }
}