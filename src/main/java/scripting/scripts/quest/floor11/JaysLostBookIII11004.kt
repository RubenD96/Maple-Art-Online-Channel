package scripting.scripts.quest.floor11

import client.Client
import client.player.quest.reward.ExpQuestReward
import client.player.quest.reward.FameQuestReward
import client.player.quest.reward.MesoQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.playerName
import scripting.dialog.SpeakerType
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([11004])
class JaysLostBookIII11004: QuestScript() {
    override fun execute(c: Client) {
        execute(c, 1012109) {
            with(it) {
                sendMessage(
                    "Ok, one last thing!",
                    next = { firstDialogue() }
                )
            }
        }
    }

    override fun DialogContext.onEnd() {
        endMessage("Unfortunate, after all we've been through.")
    }

    private fun DialogContext.firstDialogue() {
        sendMessage(
            "Hold on, you said you will tell me about that theory of yours!.",
            speaker =  SpeakerType.NpcReplacedByUser,
            next = {secondDialogue()}
        )
    }

    private fun DialogContext.secondDialogue() {
        sendMessage(
            "Right, of course, sorry!",
            next = { thirdDialogue() }
        )
    }

    private fun DialogContext.thirdDialogue() {
        sendMessage(
            "My theory is that the whole city sank!" +
            "\r\nthe original location of the city was on an island, far away from here." +
            "\r\nAnd one day just like that the whole island vanished of land, leaving no trace of it every existing",
            next = { fourthDialogue() }
        )
    }

    private fun DialogContext.fourthDialogue() {
        sendMessage(
            if (c.character.highestFloor >= 15) {
                "That sounds insane, but i've already helped you twice so whatever." +
                "\r\nwhat else do you need?."
            } else {
                "(I wonder if it's the sunken city he's talking about...)" +
                "\r\nOkay I'll help you, what else do you need?"
            },
            next = { fifthDialogue() }
        )
    }

    private fun DialogContext.fifthDialogue() {
        sendMessage(
            "I need a string to bind back the pages and the leather cover," +
            "\r\nthat should not be that complicated!" +
            "\r\nwill you help me one last time?",
            accept = { onAccept() },
            decline = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "Thank you so much!, you are so kind $playerName" +
                "\r\nYou should be able to obtain a string from any monster in taft, good luck!"
        )
    }

    override fun finish(c: Client) {
        execute(c, 1012109) {
            with(it) {
                sendMessage(
                    "Is that the string I asked for?",
                    next = { endingDialogue() }
                )
            }
        }
    }

    private fun DialogContext.endingDialogue() {
        postRewards(
            listOf(
                ExpQuestReward(80000),
                MesoQuestReward(75000),
                FameQuestReward(5)
            ),
        "The book is now wrapped and safe" +
            "\r\nThank you so much!",
            take = mapOf(4007012 to 1),
        )
    }


}