package scripting.scripts.quest.floor12

import client.Client
import client.player.quest.reward.ExpQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.SpeakerType
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript
import java.awt.Dialog

@Quest([12003])
class Blurpblurpbluprbluprubl12004: QuestScript() {
    override fun DialogContext.onEnd() {
        endMessage(
            "BlurpBlurpBlurp"
        )
    }

    override fun execute(c: Client) {
        execute(c, 3003422) {
            with(it) {
                sendMessage(
                    "blurpblurpblurpblurpblurp",
                    next = { playerConfused() }
                )
            }
        }
    }

    private fun DialogContext.playerConfused() {
        sendMessage(
            "(I can't understand them, what should I do?)",
            next = { fishStillNotUnderstandable() },
            speaker = SpeakerType.NpcReplacedByUser,
        )
    }

    private fun DialogContext.fishStillNotUnderstandable() {
        startQuest()
        sendMessage(
            "BlurplurpblurpBlurpBlurp!",
            )
    }

    override fun finish(c: Client) {
        execute(c, 3003422) {
            with(it) {
                postRewards(
                    listOf(
                        ExpQuestReward(420),
                    ),
                    "Something fishy is going on here, I must find a way to understand them!",
                    speaker = SpeakerType.NpcReplacedByUser
                )
            }
        }
    }
}