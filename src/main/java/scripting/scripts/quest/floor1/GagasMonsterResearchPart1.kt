package scripting.scripts.quest.floor1

import client.Client
import client.player.quest.reward.ExpQuestReward
import client.player.quest.reward.MesoQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([1002])
class GagasMonsterResearchPart1: QuestScript() {
    override fun execute(c: Client) {
        execute(c, 9000021) {
            with(it){
                sendMessage(
                    "Hello there adventurer! I need some help with my research, I am not very strong, can I commission you to help me kill a few mobs for me?",
                    accept = { onAccept() },
                    decline = { onDecline() }
                )
            }
        }
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "Oh, that's too bad.\n\nTalk to me again if you change your mind!",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest();
        sendMessage(
            "Thanks for doing this, you will aid my research a lot!" +
                "\r\nPlease hunt down a few of every mob you find on Floor 1 and come back to me!",
            ok = { onEnd() }
        )
    }

    override fun finish(c: Client) {
        execute(c, 9000021) {
            with(it) {
                sendMessage(
                    "Alright, now that you have the data, I need to do a bit more research.",
                    next = {completeQuest()},
                )
            }
        }
    }
    private fun DialogContext.completeQuest(){
        postRewards(
            listOf(
                ExpQuestReward(1500),
                MesoQuestReward(2000)
            ),
            "Please accept this token of my appreciation. Thank you for your help! Here is your reward."
        )

    }
}
