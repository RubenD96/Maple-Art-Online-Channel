package scripting.scripts.quest.floor1

import client.Client
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.itemImage
import scripting.dialog.DialogUtils.itemName
import scripting.dialog.DialogUtils.red
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript


@Quest([1006])
class NoQuestionsAsked: QuestScript() {
    override fun execute(c: Client) {
        execute(c, 2071002) {
            it.sendMessage(
                "Hello, I need some items but I don't need your help if you're gonna ask me any questions, deal?",
                yes = {it.onYes()},
                no = {it.onNo()}
            )
        }
    }

    private fun DialogContext.onNo() {
        sendMessage(
            "Yeah, I'll find somebody else that will not ask questions.",
            ok = {onEnd()}
        )
    }

    private fun DialogContext.onYes() {
        startQuest();
        sendMessage(
            "Good, here is a list of what I need. And remember, " +
                    "${"no questions!".red()} " +
                    "\\n\\n ${4032027.itemImage()} ${"10".blue()} ${4032027.itemName().red()}" +
                    "\\n${4032028.itemImage()} ${"10".blue()} ${4032028.itemName().red()}" +
                    "\\n${4032029.itemImage()} ${"10".blue()} ${4032029.itemName().red()}" +
                    "\\n${4032030.itemImage()} ${"10".blue()} ${4032030.itemName().red()}",
            ok = {onEnd()}
        )
    }

    override fun finish(c: Client) {
        execute(c, 2071002) {
            it.finishQuest()
            it.sendMessage(
                "Alright thanks...",
                next = {it.whyAreYouStillHere()}
            )
        }
    }
    private fun DialogContext.whyAreYouStillHere() {
        sendMessage(
            "Why are you still here?",
            ok = {onEnd()}
        )
    }

}