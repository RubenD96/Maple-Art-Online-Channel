package scripting.scripts.quest.floor1

import client.Client
import client.player.quest.reward.ExpQuestReward
import client.player.quest.reward.ItemQuestReward
import client.player.quest.reward.MesoQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.bold
import scripting.dialog.DialogUtils.red
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript
import java.awt.Dialog

@Quest([1007])
class GrandmaYeonsLastWish1007 : QuestScript() {
    override fun execute(c: Client) {
        execute(c, 2071007) {
            with(it){
                sendMessage(
                    "Hello young warrior. Would you mind helping out an old lady retrieve her lost ring?",
                    next = {it.firstLoreDialog()},
                )
            }
        }
    }
    private fun DialogContext.firstLoreDialog() {
        sendMessage(
            "A long time ago when I was younger, I went on an expedition with my lover. We found a mystical tree cave east of Tolbana.",
            next = {secondLoreDialog()}
        )
    }
    private fun DialogContext.secondLoreDialog() {
        sendMessage(
            "But when we entered this tree cave, we got attacked by mushrooms from all angles, and we barely made it out. It was on our attempt to escape that I had lost my ring.",
            next = {grandmaYeonRequest()}
        )
    }

    private fun DialogContext.grandmaYeonRequest() {
        sendMessage(
            "It would mean the world to me if you could retrieve my ring from the ${"Zombie Mushmom".red()} within the cave!" +
                    "\r\nCan you please help?",
            accept = {onAccept()},
            decline = {onDecline()}
        )
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "Oh... looks like I'll have to find another warrior to help fulfill my final wish..",
            ok = {onEnd()}
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "Thank you very much for accepting my request!!",
            ok = {onEnd()}
        )
    }

    override fun finish(c: Client) {
        execute(c, 2071007) {
            it.sendMessage(
                "Is that... my ring..? ...Bless your soul, thank you so much!",
                next = {it.completeQuest()}
            )
        }
    }

    private fun DialogContext.completeQuest() {
        postRewards(
            listOf(
                ExpQuestReward(12000),
                MesoQuestReward(4000),
                ItemQuestReward(1112413, 1)
            ),
            "I'm at the end of my days. You fulfilled my wish to see my ring one last time. As a reward, I want you to keep the ring!",
        )
    }
}