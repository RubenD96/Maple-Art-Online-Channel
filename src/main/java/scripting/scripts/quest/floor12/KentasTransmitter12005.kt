package scripting.scripts.quest.floor12

import client.Client
import client.player.quest.reward.ItemQuestReward
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([12005])
class KentasTransmitter12005: QuestScript() {
    override fun execute(c: Client) {
        //No reason to implement.
    }

    override fun finish(c: Client) {
        execute(c, 2060005) {
            with(it) {
                postRewards(
                    listOf(
                        ItemQuestReward(1032076, 1)
                    ),
                "Just as we agreed, here is the transmitter." +
                    "\r\nRemember to wear it so you can speak with the fish!",
                )
            }
        }
    }
}