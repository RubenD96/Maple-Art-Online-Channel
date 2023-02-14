package scripting.scripts.quest.floor11

import client.Client
import client.player.quest.reward.ExpQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.bold
import scripting.dialog.DialogUtils.playerName
import scripting.dialog.SpeakerType
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([11005])
class TDSTheBeginning11005 : QuestScript() {
    override fun execute(c: Client) {
        execute(c, 2081008) {
            //NoStart
        }
    }

    override fun finish(c: Client) {
        execute(c, 2081008) {
            with(it) {
                sendSimple(
                    "Hey $playerName." +
                        "\r\nYes, your name is already known to us." +
                        "\r\nWe were expecting your arrival.",
                    selections = linkedMapOf(
                        "\r\nWhat? We? Who are you?".bold() to {firstDialogue()},
                    ),
                )
            }
        }
    }

    private fun DialogContext.firstDialogue() {
        sendMessage(
            "We are the spiritual representation of nine out of ten dragons that used to rule these lands up until the ascension." +
            "\r\n\r\nThe evil being that cursed the lands took over one of ours, empowering him with strength beyond belief." +
            "\r\n\r\nWe had to seal him away, sacrificing part of ourselves in the process and sealing almost the entirety of our souls away in the process." +
            "\r\n\r\nWhat you currently see is just a small fragment of what used to be nine magnificent dragons.",
            next = {secondDialogue()}
        )
    }

    private fun DialogContext.secondDialogue() {
        sendSimple(
            "(Ascension? does it mean before the rise of Aincrad?)",
            selections = linkedMapOf(
                "\r\nSo what do you need from me?" to {finalYesNoDialogue()}
            ),
            speaker = SpeakerType.NpcReplacedByUser
        )
    }

    private fun DialogContext.finalYesNoDialogue() {
        sendMessage(
        "Only the strong can see us, so that makes you special... and perhaps strong enough to defeat our sibling." +
            "\r\nWould you help release the curse and free our souls?",
            yes = {onYes()}
        )
    }

    private fun DialogContext.onYes() {
        postRewards(
            listOf(
                ExpQuestReward(60000)
            ),
        "Very well." +
            "\r\n\r\nFor us to know you are capable, there are two tests." +
            "\r\nThe first will test your wisdom." +
            "\r\nThe second will test your strength." +
            "\r\nPassing the tests will serve as a proof of your power, and that we can trust you with this special task." +
            "\r\nLet the tests begin.",
        )
    }
}