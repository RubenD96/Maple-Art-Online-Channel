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


@Quest([1006])
class NoQuestionsAsked: QuestScript() {
    override fun execute(c: Client) {
        execute(c, 2071002) {
            with(it){
                sendMessage(
                    "Hello, I need some items but I don't need your help if you're gonna ask me any questions, deal?",
                    yes = {it.onYes()},
                    no = {it.onNo()}
                )
            }
        }
    }

    private fun DialogContext.onNo() {
        sendMessage(
            "Yeah, I'll find somebody else that will not ask questions.",
            ok = {onEnd()}
        )
    }

    private fun DialogContext.onYes() {
        startQuest()
        sendMessage(
            "Good, here is a list of what I need. And remember, " +
                    "${"no questions!".red()} " +
                    "\n\n${4032027.itemImage()} ${"10".blue()} ${4032027.itemName().blue()}" +
                    "\n\n${4032028.itemImage()} ${"10".blue()} ${4032028.itemName().blue()}" +
                    "\n\n${4032029.itemImage()} ${"10".blue()} ${4032029.itemName().blue()}" +
                    "\n\n${4032030.itemImage()} ${"10".blue()} ${4032030.itemName().blue()}",
            ok = {onEnd()}
        )
    }

    override fun finish(c: Client) {
        execute(c, 2071002) {
            with(it){
                sendMessage(
                    "Alright thanks...",

                    next = {it.completeQuest()}
                )
            }
        }
    }
    private fun DialogContext.completeQuest() {
        postRewards(
            listOf(
                ExpQuestReward(10000),
                MesoQuestReward(20000)
            ),
            "Why are you still here?",
        take = mapOf(
            4032030 to 10,
            4032029 to 10,
            4032028 to 10,
            4032027 to 10)
        )
    }
}