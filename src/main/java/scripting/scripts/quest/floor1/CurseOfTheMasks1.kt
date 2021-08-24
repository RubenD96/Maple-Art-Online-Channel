package scripting.scripts.quest.floor1

import client.Client
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
            it.sendMessage(
                "Help... me...",
                accept = {it.onAccept()},
                decline = {it.onDecline()}
            )
        }
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "Please... Break... Curse... Bring me ${"100".red()} ${4000196.itemName().blue()} ${4000196.itemImage()}",
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
            it.finishQuest()
            it.sendMessage(
                "Thanks kind... warrior, I... feel a bit... better now" +
                        "\\nBut... its not over!",
                ok = {it.onEnd()}
            )
        }
    }
}