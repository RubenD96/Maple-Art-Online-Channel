package scripting.scripts.quest.floor10

import client.Client
import client.player.quest.reward.ExpQuestReward
import client.player.quest.reward.MesoQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.itemImage
import scripting.dialog.DialogUtils.red
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([10008])
class ExterminateTheBunnies : QuestScript() {

    override fun execute(c: Client) {
        execute(c, 2030002) {
            with(it) {
                sendMessage(
                    "Those darn bunnies are back it again. Help me out once more?",
                    accept = { onAccept() },
                    decline = { onDecline() }

                )
            }
        }
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "That's a shame... I was going to give you something nice...",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "I have to get home and those bunnies are in the way. Yeah, I thought they were cute and all, but I've had enough of this place. Get rid of them! I need you to get rid of ${"200 Moon Bunnies".red()}.",
            ok = { onEnd() }
        )
    }


    override fun finish(c: Client) {
        execute(c, 2030002) {
            with(it) {
                sendMessage(
                    "Thank you for getting rid of those annoying bunnies. I can now go on my merry way back home.",
                    next = { completeQuest() }
                )
            }
        }
    }

    private fun DialogContext.completeQuest() {
        postRewards(
            listOf(
                ExpQuestReward(22200),
                MesoQuestReward(100000)
            ),
        )
    }
}