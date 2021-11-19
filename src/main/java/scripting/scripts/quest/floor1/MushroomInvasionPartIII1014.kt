package scripting.scripts.quest.floor1

import client.Client
import client.player.quest.reward.ExpQuestReward
import client.player.quest.reward.MesoQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.bold
import scripting.dialog.DialogUtils.red
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([1014])
class MushroomInvasionPartIII1014 : QuestScript() {

    override fun execute(c: Client) {
        execute(c, 2071009) {
            with(it) {
                sendMessage(
                    "After eliminating the ${"Zombie Mushmom".red()}, we've discovered that her ${"Poisonous Mushroom".blue()} is very toxic, but incredibly useful as compost when mixing it with some ${"seedlings".blue()}!" +
                            "\r\nCould you bring us some?",
                    accept = { onAccept() },
                    decline = { onDecline() }

                )
            }
        }
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "We ${"NEED".bold().blue()} that compost, it is very valuable. Please do reconsider.",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "Please bring me ${"5 Poisonous Mushrooms".blue()} and ${"50 Seedlings".blue()}.",
            ok = { onEnd() }
        )
    }


    override fun finish(c: Client) {
        execute(c, 2071009) {
            with(it) {
                postRewards(
                    listOf(
                        ExpQuestReward(5000),
                        MesoQuestReward(10000)
                    ),
                    "Our village is eternally grateful!",
                    take = mapOf(
                        4000176 to 5,
                        4000195 to 50)
                )
            }
        }
    }
}