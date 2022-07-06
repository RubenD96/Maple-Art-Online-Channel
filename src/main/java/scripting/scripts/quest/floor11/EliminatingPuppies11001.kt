package scripting.scripts.quest.floor11

import client.Client
import client.player.quest.reward.ExpQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.bold
import scripting.dialog.DialogUtils.itemImage
import scripting.dialog.DialogUtils.itemName
import scripting.dialog.DialogUtils.playerName
import scripting.dialog.DialogUtils.red
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([11001])

class EliminatingPuppies11001 : QuestScript() {

    override fun execute(c: Client) {
        execute(c, 1012108) {
            with(it) {
                sendMessage(
                    "Hey there ${playerName.blue()}, can you help me make a necklace for my mother?" +
                            "\r\nHer birthday is coming soon and I want my present to be special!",
                    next = { firstDialogue() }
                )
            }
        }
    }

    private fun DialogContext.firstDialogue() {

        sendSimple(
            "I want to make her a necklace made out of puppies teeth, but I can't kill them myself," +
                    "\r\n${"can you do it???".blue()}",
            selections = linkedMapOf(
                "KILLING PUPPIES ARE YOU INSANE? NO!".blue() to {onDecline()},
                "Sure, I'm a heartless bastard.".blue() to {onAccept()}
            )
        )
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "That's a shame... I was going to give you something nice...",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "That's great!" +
                    "\r\nThen go ahead and ${"slaughter".bold().red()} 50 Brown Puppies and obtain 25 ${4000078.itemName()}" +
                    "\r\nIt should look like this: ${4000078.itemImage()}",
            ok = { onEnd() }
        )
    }

    override fun finish(c: Client) {
        execute(c, 1012108) {
            with(it) {
                sendMessage("I see you obtained the materials for the necklace, thank you so much $playerName!",
                next = { endingDialogue() }
                )
            }
        }
    }

    private fun DialogContext.endingDialogue() {
        postRewards(
            listOf(
                ExpQuestReward(44500)
            ),
            "Mom is going to be so happy!",
            take = mapOf(4000078 to 25),
        )
    }
}