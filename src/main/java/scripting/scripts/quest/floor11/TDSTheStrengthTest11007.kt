package scripting.scripts.quest.floor11

import client.Client
import client.player.quest.reward.ExpQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.red
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([11007])
class TDSTheStrengthTest11007: QuestScript() {
    override fun execute(c: Client) {
        execute(c, 2081008) {
            with(it) {
                sendMessage(
                    "In the strength test, you will have to prove yourself against the wicked creatures of this cave." +
                        "\r\nAre you ready?",
                    yes = { onAccept() }
                )
            }
        }
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
        "Very well, eliminate 150 of each creature currently present in the cave." +
            "\r\nGood luck!",
            ok = { onEnd() }
        )
    }

    override fun finish(c: Client) {
        execute(c, 2081008) {
            with(it) {
                sendMessage(
                    "We can sense that the dark forces of the cave have been weakened." +
                        "\r\nYou truly are the one!",
                    next = { firstDialogue() }
                )
            }
        }
    }

    private fun DialogContext.firstDialogue() {
        sendMessage(
        "We are going to release the seal and thus unlock the cave" +
            "\r\nOur brother, ${"Ashford".red()} is inside." +
            "\r\nAs you probably already figured out from the answers to the wisdom test, this is a mere reflection of what Ashford, The Fire Dragon, used to be." +
            "\r\nKilling him will release the darkness that set upon his soul and allow him to cross over to the other side with us.",
            next = { secondDialogue() }
        )
    }

    private fun DialogContext.secondDialogue() {
        sendMessage(
            "But know that we cant reseal the cave once it's released!" +
                "\r\nAccept only if you are willing to see this through the end" +
                "\r\nDo you accept the final challenge?",
            accept = { finalAcceptRewardsDialogue() }
        )
    }

    private fun DialogContext.finalAcceptRewardsDialogue() {
        postRewards(
            listOf(
                ExpQuestReward(90000)
            ),
            "Very well, farewell human." +
                "\r\nWe were so glad to meet you, good luck."
        )

        TODO("reset monster spawn")
        TODO("remove npc 2081008")
        TODO("force start quest 11008")
    }
}
