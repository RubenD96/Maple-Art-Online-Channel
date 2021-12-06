package scripting.scripts.quest.floor2

import client.Client
import client.player.quest.reward.ExpQuestReward
import client.player.quest.reward.MesoQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([2004])
class Bromance2004: QuestScript() {
    override fun execute(c: Client) {
        execute(c, 20001) {
            with(it) {
                sendMessage(
                    "Hey you there, do you have a minute for a fellow brother?",
                    yes = { onYes() },
                    no = { onNo() }
                )
            }
        }
    }

    private fun DialogContext.onYes() {
        startQuest()
        sendMessage(
            "I have loved Mu Young for many years but I never got the chance to tell him!" +
                    "\n\nWill you please find him and tell him I love him? I'm sure he loves me back just as much!",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onNo() {
        sendMessage(
            "I see, I guess some people still don't accept people like me...",
            ok = { onEnd() }
        )
    }

    override fun finish(c: Client) {
        execute(c, 1061014) {
            with(it) {
                sendMessage(
                    "What do you need?",
                    next = { Dialogue1() }
                )
            }
        }
    }

    private fun DialogContext.Dialogue1() {
        sendMessage(
            "Well, out with it, what is it?",
            next = { Dialogue2() }
        )
    }

    private fun DialogContext.Dialogue2() {
        sendMessage(
            "Who is Bari?",
            end = { completeQuest() }
        )
    }

    private fun DialogContext.completeQuest() {
        execute(c, 20001) {
            with(it) {
                postRewards(
                    listOf(
                        ExpQuestReward(1000),
                        MesoQuestReward(200000)
                    ),
                    "Mu Young truly said, 'Who's Bari?'",
                )
            }
        }
    }
}
