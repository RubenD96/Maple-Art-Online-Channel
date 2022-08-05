package scripting.scripts.quest.floor11

import client.Client
import client.player.quest.reward.ExpQuestReward
import client.player.quest.reward.MesoQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.playerName
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([11002])
class JaysLostBookI11002: QuestScript() {
    override fun execute(c: Client) {
        execute(c, 1012109) {
            with(it) {
                sendMessage(
                    "Phew, I almost got caught by those filthy monsters!",
                    next = { firstDialogue() }
                )
            }
        }
    }


    private fun DialogContext.firstDialogue() {
        sendMessage(
            "Wait my book, #ewhere's my book?!",
            next = { secondDialogue()},
        )
    }

    private fun DialogContext.secondDialogue() {
        sendMessage(
            "Hey you there, you look like a nice person, please help me!",
            next = { thirdDialogue()}
        )
    }

    private fun DialogContext.thirdDialogue() {
        sendMessage(
            "My name is Jay and I just came back from my uncle's house, located to the west of taft" +
                "\r\nI had a very important book with me, and I think it dropped on the way here" +
                "\r\nCan you please help me retrieve it?",
            accept = { onAccept()},
            decline = {onDecline()}
        )
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "This is important, please come back!",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
        "Thank you so much! The content of the book got scattered while I ran away." +
            "\r\nStart by looking for the book's pages, 25 pages to be exact. And please hurry!",
            ok = { onEnd() }
        )
    }

    override fun finish(c: Client) {
        execute(c, 1012108) {
            with(it) {
                sendMessage(
                    "Are those the pages from my book?",
                    next = { endingDialogue() }
                )
            }
        }
    }

    private fun DialogContext.endingDialogue() {
        postRewards(
            listOf(
                ExpQuestReward(55000),
                MesoQuestReward(75000)
            ),
            take = mapOf(4032579 to 25)
        )
        sendMessage(
        "that's wonderful..." +
            "\r\nThank you so much $playerName!",
            ok = { onEnd() },
        )
    }
}