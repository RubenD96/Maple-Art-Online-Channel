package scripting.scripts.quest.floor10

import client.Client
import client.player.quest.reward.ExpQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.itemImage
import scripting.dialog.DialogUtils.red
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([10007])
class BunnyInfestation : QuestScript() {

    override fun execute(c: Client) {
        execute(c, 2030002) {
            with(it) {
                sendMessage(
                    "Hello stranger! You up for some dirty work?",
                    accept = { onAccept() },
                    decline = { onDecline() }

                )
            }
        }
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "Good day!",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "I've been having trouble finding my way back, especially with those darn ${"moon bunnies".red()} running around everywhere. They're an exquisite creature and I want to learn more about them.\n" +
                 "Help me out by collecting their pounders and I'll be sure to give you a nice reward! I need you to collect ${4000169.itemImage()} ${100.blue()} ${"pounders".blue()} for me..",
            ok = { onEnd() }
        )
    }


    override fun finish(c: Client) {
        execute(c, 2030002) {
            with(it) {
                sendMessage(
                    "Look at all these pounders! I find it so unique that bunnies are just holding onto these. Well, thank you so much for the help.",
                    next = { completeQuest() }
                )
            }
        }
    }

    private fun DialogContext.completeQuest() {
        postRewards(
            listOf(
                ExpQuestReward(22200)
            ),
            take = mapOf(4000169 to 100)
        )
    }
}