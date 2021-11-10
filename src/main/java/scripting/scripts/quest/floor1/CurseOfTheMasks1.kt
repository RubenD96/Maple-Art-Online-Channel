package scripting.scripts.quest.floor1

import client.Client
import client.player.quest.reward.ExpQuestReward
import client.player.quest.reward.MesoQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.itemImage
import scripting.dialog.DialogUtils.itemName
import scripting.dialog.DialogUtils.red
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([1008])
class CurseOfTheMasks1: QuestScript() {
    override fun execute(c: Client) {
        execute(c, 2112009) {
            with(it){
                sendMessage(
                    "Help... me...",
                    accept = {onAccept()},
                    decline = {onDecline()}
                )
            }
        }
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "Please... Break... The curse... Bring me ${4000196.itemImage()} ${"100 Wooden Boards".blue()}.",
            ok = {onEnd()}
        )
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "Ugh..",
            ok = {onEnd()}
        )
    }

    override fun finish(c: Client) {
        execute(c, 2112009) {
            with(it){
                sendMessage(
                    "Thank you... kind... warrior, I... feel a bit... better now.",
                    next = { completeQuest()}
                )
            }
        }
    }
    private fun DialogContext.completeQuest(){
        postRewards(
            listOf(
                ExpQuestReward(10000),
                MesoQuestReward(7500)
            ),
            "But... its not over!",
            take = mapOf(4000196 to 100),
        )
    }
}