package scripting.scripts.quest.floor1

import client.Client
import client.player.quest.reward.ExpQuestReward
import client.player.quest.reward.MesoQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.red
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([1013])
class MushroomInvasionPartII1013 : QuestScript() {

    override fun execute(c: Client) {
        execute(c, 2071009) {
            with(it) {
                sendMessage(
                    "We have located the main source of the mushroom plague." +
                            "\r\nTheir leader, Zombie Mushmom, has settled in the old Mushmom house." +
                            "\r\nCan you go over there and eliminate her?",
                    accept = { onAccept() },
                    decline = { onDecline() }

                )
            }
        }
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "Please, you're our only hope.",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "Awesome, please eliminate ${"Zombie Mushmom".red()} and ${"100 Horned Mushrooms".red()}.",
            ok = { onEnd() }
        )
    }


    override fun finish(c: Client) {
        execute(c, 2071009) {
            with(it) {
                postRewards(
                    listOf(
                        ExpQuestReward(12000),
                        MesoQuestReward(5000)
                    ),
                    "Thank you for your services.\r\nHere is your well deserved reward.",
                )
            }
        }
    }
}