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
import java.awt.Dialog

@Quest([1009])
class CurseOfTheMasksII1009:QuestScript() {
    override fun execute(c: Client) {
        execute(c, 2112009) {
            it.sendMessage(
                "Please help... me one more... time!",
                accept = {it.onAccept()},
                decline = {it.onDecline()}
            )
        }
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "Ugh..",
            ok = {onEnd()}
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "The curse... it's not fully... gone, bring me ${4000197.itemImage()} ${"100 Slates".blue()}",
            ok = {onEnd()}
        )
    }

    override fun finish(c: Client) {
        execute(c, 2112009) {
            with(it){
                sendMessage(
                    "Wow... The curse... I can't believe it! The curse is gone!",
                    next = { completeQuest()}
                )
            }
        }
    }
    private fun DialogContext.completeQuest(){
        postRewards(
            listOf(
                ExpQuestReward(15000),
                MesoQuestReward(12500),
            ),
        take = mapOf(
            4000197 to 100)
        )
    }
}