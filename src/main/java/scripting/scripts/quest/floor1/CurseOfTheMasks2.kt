package scripting.scripts.quest.floor1

import client.Client
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.itemImage
import scripting.dialog.DialogUtils.itemName
import scripting.dialog.DialogUtils.red
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript
import java.awt.Dialog

@Quest([1009])
class CurseOfTheMasks2:QuestScript() {
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
            "The curse... its not fully... gone, bring me ${"100".red()} ${4000197.itemName().blue()} ${4000197.itemImage()}",
            ok = {onEnd()}
        )
    }

    override fun finish(c: Client) {
        execute(c, 2112009) {
            it.finishQuest()
            it.sendMessage(
                "Wow the curse... I cant believe it! The curse is gone!",
                ok = {it.onEnd()}
            )
        }
    }
}