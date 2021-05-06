package scripting.scripts.quest

import client.Client
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.itemImage
import scripting.dialog.DialogUtils.itemName
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([1000])
class RainingBoars : QuestScript() {

    override fun execute(c: Client) {
        execute(c, 12101) {
            it.sendMessage(
                "I want to make a beautiful necklace but I am missing something...\r\n" +
                        "Maybe you can give me some boar teeth for the necklace? I'll reward you afterwards!",
                accept = { it.onAccept() },
                decline = { it.onDecline() }
            )
        }
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "Oh, that's too bad.\n" +
                    "Talk to me again if you change your mind!",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "Wow thanks for doing this, I can't tell you how grateful I am.\r\n" +
                    "Please do it quick though, I'm in a hurry!",
            next = { startInfo() }
        )
    }

    private fun DialogContext.startInfo() {
        sendMessage(
            "The teeth look like this by the way!\r\n\r\n" +
                    "${4000020.itemImage()} ${10.blue()} ${4000020.itemName()}",
            ok = { onEnd() }
        )
    }

    override fun finish(c: Client) {
        execute(c, 12101) {
            it.sendMessage(
                "What? You brought the teeth? Okay, let's see...",
                next = { it.completeQuest() }
            )
        }
    }

    private fun DialogContext.completeQuest() {
        sendMessage(
            "Wow thanks a lot, you really made my day! I hope to see you around more often!",
            ok = {
                // give rewards
                completeQuest()
            }
        )
    }
}