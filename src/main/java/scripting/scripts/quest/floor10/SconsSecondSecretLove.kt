package scripting.scripts.quest.floor10

import client.Client
import client.player.quest.reward.ExpQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.playerName
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([10004])
class SconsSecondSecretLove : QuestScript() {

    override fun execute(c: Client) {
        execute(c, 9102000) {
            with(it) {
                sendMessage(
                    "Hey, ${playerName.blue()}. I need your help again. Whaddya say?",
                    accept = { onAccept() },
                    decline = { onDecline() }

                )
            }
        }
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "C'mon! It's special this time. Alright, fine.",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "Okay, listen. I was wrong the entire time! I shouldn't have gone for ${"Neri".blue()}, that was stupid of me. I knew she didn't like me. My true love is actually for ${"Nuri".blue()}. I know, I know. I know what you're thinking, but you're just going to have to trust me on this one.\n" +
                    "So here's the problem... I accidentally dropped my letter for ${"Nuri".blue()} while I was walking around town. Can you blame me? I needed some inspiration. Could you help me find it? It's got to be somewhere on this floor.",
            ok = { onEnd() }
        )
    }


    override fun finish(c: Client) {
        execute(c, 9102000) {
            with(it) {
                sendMessage(
                    "You found it! Great, thanks. I'm just going to add some finishing touches. Then maybe you'll help give it to her?",
                    next = { completeQuest() }
                )
            }
        }
    }

    private fun DialogContext.completeQuest() {
        postRewards(
            listOf(
                ExpQuestReward(20900)
            ),
        )
    }
}