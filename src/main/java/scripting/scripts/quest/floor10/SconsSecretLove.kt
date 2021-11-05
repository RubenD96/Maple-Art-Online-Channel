package scripting.scripts.quest.floor10

import client.Client
import client.player.quest.reward.ExpQuestReward
import client.player.quest.reward.MesoQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.itemImage
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([10003])
class SconsSecretLove : QuestScript() {

    override fun execute(c: Client) {
        execute(c, 9102000) {
            with(it) {
                sendMessage(
                    "H-hey! How's it going? Want to help me with something?",
                    accept = { onAccept() },
                    decline = { onDecline() }

                )
            }
        }
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "Oh. Alright then, forget I asked.",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "Really?! Thanks!" +
                    "See that girl over there, ${"Nuri".blue()}? Well, I have the biggest crush on her! Could you give this ${4032280.itemImage()} letter to her for me?",
            ok = { onEnd() }
        )
    }


    override fun finish(c: Client) {
        execute(c, 9102000) {
            with(it) {
                sendMessage(
                    "Yay! You gave it to her... What did she say?" +
                            "Oh... How unfortunate. Well, I heard ${"Nuri".blue()} just recently became single...",
                    next = { completeQuest() }
                )
            }
        }
    }

    private fun DialogContext.completeQuest() {
        postRewards(
            listOf(
                ExpQuestReward(6500),
                MesoQuestReward(10000)
            ),
            take = mapOf(4032280 to 1)
        )
    }
}