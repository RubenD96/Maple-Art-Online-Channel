package scripting.scripts.quest.floor1

import client.Client
import client.player.quest.reward.ExpQuestReward
import client.player.quest.reward.MesoQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.red
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([1012])
class MushroomInvasionPartI1012 : QuestScript() {

    override fun execute(c: Client) {
        execute(c, 2071009) {
            with(it) {
                sendMessage(
                    "The mushrooms are spreading and they are not stopping!" +
                            "\r\nCan you help me take care of it?",
                    accept = { onAccept() },
                    decline = { onDecline() }

                )
            }
        }
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "You'll be missing out of a once in a lifetime opportunity!",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "Please kill ${"100 Green Mushrooms".red()} and ${"100 Horned Mushrooms".red()}.",
            ok = { onEnd() }
        )
    }


    override fun finish(c: Client) {
        execute(c, 2071009) {
            with(it) {
                postRewards(
                    listOf(
                        ExpQuestReward(7000),
                        MesoQuestReward(20000)
                    ),
                    "Here is your reward, but please come back to me sometime later!",
                )
            }
        }
    }
}