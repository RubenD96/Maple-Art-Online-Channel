package scripting.scripts.quest.floor10

import client.Client
import client.player.quest.reward.ExpQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.playerName
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([10006])
class SconsFinalResort : QuestScript() {

    override fun execute(c: Client) {
        execute(c, 9102000) {
            with(it) {
                sendMessage(
                    "${playerName.blue()}, ${playerName.blue()}! I know you're probably tired of me now, but I have one last task for you. You up for it?",
                    accept = { onAccept() },
                    decline = { onDecline() }

                )
            }
        }
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "Ok ok ok, I get it. You&apos;ve had enough of me.",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "Hear me out, I think I was wrong the entire time. I heard there was another fairy in town named ${"Kriel".blue()}. And I think she likes me. Could you talk to her for me? See if the rumors are true?" +
                    "Thanks, ${playerName.blue()}. I appreciate it.",
            ok = { onEnd() }
        )
    }


    override fun finish(c: Client) {
        execute(c, 9102000) {
            with(it) {
                sendMessage(
                    "So, what&apos;d she say?" +
                            "A lunch box? She gave me a lunch box?!?!",
                    next = { completeQuest() }
                )
            }
        }
    }

    private fun DialogContext.completeQuest() {
        postRewards(
            listOf(
                ExpQuestReward(36000),
            ),
            "Wow, she truly is the one. Thank you so much for talking to her for me, ${playerName.blue()}.",
            take = mapOf(1032074 to 1)
        )
    }
}