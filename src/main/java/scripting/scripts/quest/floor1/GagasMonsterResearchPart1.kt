package scripting.scripts.quest.floor1

import client.Client
import scripting.dialog.DialogContext
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([1002])
class GagasMonsterResearchPart1: QuestScript() {
    override fun execute(c: Client) {
        execute(c, 9000021) {
            it.sendMessage(
                "Hello there adventurer! I need some help with my research, I am not very strong so I want you to kill a few mobs for me!",
                accept = { it.onAccept() },
                decline = { it.onDecline() }
            )
        }
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "Oh, that's too bad.\\nTalk to me again if you change your mind!",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest();
        sendMessage(
            "Thanks for doing this, you will help my research a lot!" +
                "\r\nPlease hunt down a few of every mob you find on Floor 1 and come back to me!",
            ok = { onEnd() }
        )
    }

    override fun finish(c: Client) {
        execute(c, 9000021) {
            it.sendMessage(
                "Alright now that you have the data, I need to research a bit. Please accept this token of my appreciation",
                ok = {it.finishQuest()}
            )
        }
    }
}