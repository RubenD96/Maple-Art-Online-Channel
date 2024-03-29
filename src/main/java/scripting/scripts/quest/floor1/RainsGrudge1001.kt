package scripting.scripts.quest.floor1

import client.Client
import client.player.quest.reward.ExpQuestReward
import client.player.quest.reward.ItemQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.mobName
import scripting.dialog.DialogUtils.red
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript


@Quest([1001])
class RainsGrudge1001 : QuestScript() {

    override fun execute(c: Client) {
        execute(c, 12101) {
            it.sendMessage(
                "Hello there, have I ever told you that I truly hate wolves? Well I do.",
                accept = { it.onAccept() },
                decline = { it.onDecline() }
            )
        }
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "That's a shame, I hope you reconsider! The less wolves, the better...",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage("Please take down ${25.red()} wolves so I can sleep better tonight!",
            next = { postAcceptDialog() }
        )
    }

    private fun DialogContext.postAcceptDialog() {
        sendMessage(
            "After taking down ${25.red()} wolves, come back to me and I'll reward you.",
            ok = { onEnd() }
        )
    }

    override fun finish(c: Client) {
        execute(c, 12101) {
            it.sendMessage(
                "Thanks so much for doing this for me!\nHere's your reward!",
                next = { it.completeQuest() }
            )
        }
    }

    private fun DialogContext.completeQuest(){
        postRewards(
            listOf(
                ExpQuestReward(500),
                ItemQuestReward(3010001,1)
            ),
            "Thanks a lot for taking down ${"25 wolves".red()}, I will sleep better tonight!",
        )
    }
}