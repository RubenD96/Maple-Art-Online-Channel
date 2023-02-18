package scripting.scripts.quest.floor12

import client.Client
import client.player.quest.reward.ExpQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.SpeakerType
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([12006])
class TheUndeadInvasionI12006: QuestScript() {
    override fun DialogContext.onEnd() {
        endMessage(
            "THE UNDEAD ARE EVERYWHERE."
        )
    }
    override fun execute(c: Client) {
        execute(c, 9201096) {
            with(it) {
                sendMessage(
                    "Hello there, how ar...",
                    next = { firstNpcPanic() },
                    speaker = SpeakerType.NpcReplacedByUser
                )
            }
        }
    }

    private fun DialogContext.firstNpcPanic() {
        sendMessage(
        "THE DAY HAS COME, THE UNDEAD ARE RISING" +
            "\r\nWE HAVE TO RUN!",
            next = { firstPlayerCalmReply() }
        )
    }

    private fun DialogContext.firstPlayerCalmReply() {
        sendMessage(
            "Hold on a second, relax, whats wrong?",
            next = { secondNpcPanic() },
            speaker = SpeakerType.NpcReplacedByUser
        )
    }

    private fun DialogContext.secondNpcPanic() {
        sendMessage(
        "I was exploring around the caves over here and found this cave, but it looked different..." +
            "\r\nAND THEY ARE EVERYWHERE, ONLY BONES AND NO FLESH AND AND #e*heavy breathing*",
            next = { secondPlayerCalmReply() }
        )
    }

    private fun DialogContext.secondPlayerCalmReply() {
        sendMessage(
            "Hey HEY! RELAX!" +
            "\r\nI can help you, but you need to calm down first!" +
            "\r\nHow many creatures were in the cave?",
            next = { npcHelpRequest() },
            speaker = SpeakerType.NpcReplacedByUser
        )
    }

    private fun DialogContext.npcHelpRequest() {
        sendMessage(
            "There were hundreds of those boney creatures! we have to hold them from expanding outside the cave." +
            "\r\nCan you kill some of those bone fish until We figure out how to handle them? the whole city is at risk!",
            accept = { onAccept() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
        "Thank you thank you THANK YOU" +
            "\r\nI'll think of a plan while you stall for time!"
        )
    }

    override fun finish(c: Client) {
        execute(c, 9201096) {
            with(it) {
                sendMessage(
                    "Oh no, already done?",
                    next = { finalDialogue() }
                )
            }
        }
    }

    private fun DialogContext.finalDialogue() {
        postRewards(
            listOf(
                ExpQuestReward(85000)
            ),
            "That was faster then I expected..."
        )
    }
}