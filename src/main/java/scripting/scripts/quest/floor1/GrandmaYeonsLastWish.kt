package scripting.scripts.quest.floor1

import client.Client
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.bold
import scripting.dialog.DialogUtils.red
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript
import java.awt.Dialog

@Quest([1007])
class GrandmaYeonsLastWish : QuestScript() {
    override fun execute(c: Client) {
        execute(c, 2071007) {
            it.sendMessage(
                "Hello young warrior. Would you mind helping out an old lady retrieve her lost ring?",
                next = {it.firstLoreDialog()},
            )
        }
    }
    private fun DialogContext.firstLoreDialog() {
        sendMessage(
            "A long time ago when I was younger I went on an expedition with my lover. We found a mystical tree cave east of Tolbana.",
            next = {secondLoreDialog()}
        )
    }
    private fun DialogContext.secondLoreDialog() {
        sendMessage(
            "Inside the tree cave we got attacked by mushrooms from all angles, and as we barely made it out, I lost my ring that day.",
            next = {grandmaYeonRequest()}
        )
    }

    private fun DialogContext.grandmaYeonRequest() {
        sendMessage(
            "It would mean the world to me if you could retrieve my ring from the Zombie Mushmom within the cave!" +
                    "\r\nCan you please help?",
            accept = {onAccept()},
            decline = {onDecline()}
        )
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "Oh... looks like I'll have to find another warrior to help me fulfill my final wish..",
            ok = {onEnd()}
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "Thank you very much for helping me complete my final wish!!",
            ok = {onEnd()}
        )
    }

    override fun finish(c: Client) {
        execute(c, 2071007) {
            it.sendMessage(
                "Is that the ring I see!.. Thank you so much!",
                next = {it.finishQuestDialog()}
            )
        }
    }

    private fun DialogContext.finishQuestDialog() {
        finishQuest()
        sendMessage(
            "I'm at the end of my days. And you fulfilled my wish to see the ring one last time. As a reward I want you to keep the ring!",
            ok = {onEnd()}
        )
    }
}