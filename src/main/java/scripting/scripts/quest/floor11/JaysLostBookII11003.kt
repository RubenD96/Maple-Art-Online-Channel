package scripting.scripts.quest.floor11

import client.Client
import client.player.quest.reward.ExpQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.SpeakerType
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([11002])
class JaysLostBookII11003: QuestScript() {
    override fun execute(c: Client) {
        execute(c, 1012108) {
            with(it) {
                sendMessage(
                    "First of all, thanks again for retrieving the lost pages!",
                    next = { firstDialogue() }
                )
            }
        }
    }

    private fun DialogContext.firstDialogue() {
        sendMessage(
        "Now, can you help me gather 15 leathers to make a new cover? I want to keep the book safe",
            next = { secondDialogue() },
        )
    }

    private fun DialogContext.secondDialogue() {
        sendMessage(
            "Sure, But what makes this book so important to you",
            speaker = SpeakerType.NpcReplacedByUser,
            next = { thirdDialogue() }
        )
    }

    private fun DialogContext.thirdDialogue() {
        sendMessage(
        "This book contains my research about an ancient city that once existed in this world, but one day it just disappeared off the land." +
            "\r\nI have a theory, but I'm still looking for more evidence to support it.",
            next = { fourthDialogue() }
        )
    }

    private fun DialogContext.fourthDialogue() {
        sendMessage(
            "Whats the theory if I may ask?",
            speaker = SpeakerType.NpcReplacedByUser,
            next = { fifthDialogue() }
        )
    }

    private fun DialogContext.fifthDialogue() {
        sendMessage(
        "I'll tell you only if you first help me make the cover" +
            "\r\ndeal?",
            speaker = SpeakerType.NpcReplacedByUser,
            accept = { onAccept() },
            decline = { onDecline() }
        )
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "But my book!",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "Great! collect 15 leathers and bring them back to me",
            ok = { onEnd() }
        )
    }

    override fun finish(c: Client) {
        execute(c, 1012108) {
            with(it) {
                sendMessage(
                    "Did you bring the leather I asked for?",
                    next = { endingDialogue() }
                )
            }
        }
    }

    private fun DialogContext.endingDialogue() {
        finishQuest()
        postRewards(
            listOf(
                ExpQuestReward(45000)
            ),
            take = mapOf(4000021 to 15)
        )
        sendMessage(
            "Thank you so much!",
            ok = { onEnd() }
        )
    }
}