package scripting.scripts.quest.floor10

import client.Client
import client.player.quest.reward.ExpQuestReward
import client.player.quest.reward.MesoQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.bold
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([10005])
class NurisLetter : QuestScript() {

    override fun execute(c: Client) {
        execute(c, 9102000) {
            with(it) {
                sendMessage(
                    "You're back! Do you think you could just give the letter to ${"Nuri".blue()}?",
                    accept = { onAccept() },
                    decline = { onDecline() }

                )
            }
        }
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "No? Ok, well, thanks for finding it anyway.",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        c.character.gainItem(4007009)
        sendMessage(
            "Cool, here you go. Fingers crossed!",
            ok = { onEnd() }
        )
    }


    override fun finish(c: Client) {
        execute(c, 9102000) {
            with(it) {
                sendMessage(
                    "Did she take it? Awesome!",
                    next = { completeQuest() }
                )
            }
        }
    }

    private fun DialogContext.completeQuest() {
        postRewards(
            listOf(
                ExpQuestReward(20900),
                MesoQuestReward(50000)
            ),
            "Wait, she didn't say anything? Like ${"nothing".bold()}?? What in the world is up with these girls..."
        )
    }
}