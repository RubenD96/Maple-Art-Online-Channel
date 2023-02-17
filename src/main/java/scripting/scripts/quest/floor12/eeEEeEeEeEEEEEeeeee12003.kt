package scripting.scripts.quest.floor12

import client.Client
import client.player.quest.reward.ExpQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.SpeakerType
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript
import java.awt.Dialog

@Quest([12003])
class eeEEeEeEeEEEEEeeeee12003: QuestScript() {
    override fun DialogContext.onEnd() {
        endMessage(
            "EEeeeEEeEeeeEEe"
        )
    }

    override fun execute(c: Client) {
        execute(c, 1002009) {
            with(it) {
                sendMessage(
                    "EEEeeeEEeEEeEEe",
                    next = { playerSurprised() }
                )
            }
        }
    }

    private fun DialogContext.playerSurprised() {
        sendMessage(
            "What??",
            next = { dolphinStillNotUnderstandable() },
            speaker = SpeakerType.NpcReplacedByUser,
        )
    }

    private fun DialogContext.dolphinStillNotUnderstandable() {
        sendMessage(
            "EeeEeEeEeEeE!",
            next = { finalDialogue() },
            )
    }

    private fun DialogContext.finalDialogue() {
        startQuest()
        sendMessage(
            "I don't understand...",
            speaker = SpeakerType.NpcReplacedByUser,
        )
    }

    override fun finish(c: Client) {
        execute(c, 1002009) {
            with(it) {
                postRewards(
                    listOf(
                        ExpQuestReward(420),
                    ),
                    "eEeEEEEeeeEeEeE"
                )
            }
        }
    }
}