package scripting.scripts.quest.floor2

import client.Client
import client.player.quest.reward.ExpQuestReward
import client.player.quest.reward.MesoQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.itemImage
import scripting.dialog.DialogUtils.red
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([2002])
class FluffyBusiness2002 : QuestScript() {

    override fun execute(c: Client) {
        execute(c, 20002) {
            with(it) {
                sendMessage(
                    "Yes or no?",
                    yes = { Yes() },
                    no = { No() }

                )
            }
        }
    }

    private fun DialogContext.No() {
        sendMessage(
            "That's not yes.",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.Yes() {
        startQuest()
        sendMessage(
            "Ok, then kill ${"200 Sheep".red()} and collect ${"100 Sheep Skins"}. \n\n\nWhat are you still here for?",
            ok = { onEnd() }
        )
    }


    override fun finish(c: Client) {
        execute(c, 20002) {
            with(it) {
                postRewards(
                    listOf(
                        ExpQuestReward(17000),
                        MesoQuestReward(25000)
                    ),
                    "Yep, that seems alright. Thanks.",
                    take = mapOf(4000189 to 100)
                )
            }
        }
    }
}