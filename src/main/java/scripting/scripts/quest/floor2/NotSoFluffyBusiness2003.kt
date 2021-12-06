package scripting.scripts.quest.floor2

import client.Client
import client.player.quest.reward.ExpQuestReward
import client.player.quest.reward.MesoQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.red
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([2003])
class NotSoFluffyBusiness2003 : QuestScript() {

    override fun execute(c: Client) {
        execute(c, 20002) {
            with(it) {
                sendMessage(
                    "Oh, it's you again, well, you know how this works. Yes or no?",
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
            "Ok, then kill ${"80 Lorangs".red()} and collect ${"40 Lorang Claws".blue()}. \n\n\nGO GO GO!",
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
                    "Again, well done. Thanks.",
                    take = mapOf(4000043 to 40)
                )
            }
        }
    }
}