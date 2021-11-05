package scripting.scripts.quest.floor1

import client.Client
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.bold
import scripting.dialog.DialogUtils.red
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([1003])
class GagasMonsterResearchPart2 : QuestScript() {
    override fun execute(c: Client) {
        execute(c, 9000021) {
            it.sendMessage(
                "Oh no...",
                next = {it.firstOhNo()}
            )
        }
    }

    private fun DialogContext.firstOhNo() {
        sendMessage(
            "Oh no!!",
            next = {secondOhNo()}
        )
    }

    private fun DialogContext.secondOhNo() {
        sendMessage(
            "${"Oh no!!!!".red().bold()}",
            accept = {onAccept()},
            decline = {onDecline()}
        )
    }

    private fun DialogContext.onAccept() {
        startQuest();
        sendMessage(
            "My research uncovered a hidden location somewhere on Floor 1...",
            next = {postAcceptDialog()}
        )
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "It'll be the end of the world...",
            ok = {onEnd()}
        )
    }

    private fun DialogContext.postAcceptDialog() {
        sendMessage(
            "There is seems to be a very ${"dangerous".red().bold()} mob in that map, please take care of it before it reaches this town!",
            ok = {onEnd()}
        )
    }

    override fun finish(c: Client) {
        execute(c, 9000021) {
            it.sendMessage(
                "Wow you've saved this town!" +
                    "\\r\\nYou're a ${"hero".blue()}!! " +
                    "\\r\\nTake this reward as my eternal gratitude.",
                ok = {it.finishQuest()}
            )
        }
    }
}