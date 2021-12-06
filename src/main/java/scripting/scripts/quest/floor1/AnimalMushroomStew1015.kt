package scripting.scripts.quest.floor1

import client.Client
import client.player.quest.reward.ExpQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.SpeakerType
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([1015])
class AnimalMushroomStew1015 : QuestScript() {
    override fun execute(c: Client) {
        execute(c, 1072004) {
            with(it) {
                sendMessage(
                    "Hello there, do you mind helping me prepare a good meal? I'm starving!",
                    next = { Stew()},
                    speaker = SpeakerType.NpcReplacedByUser
                )
            }
        }
    }
    private fun DialogContext.Stew() {
        sendMessage(
            "Of course young warrior, if you give me the proper ingredients I can make you a good stew!",
            accept = { onAccept() },
            decline = { onDecline() }
        )

    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "May the forest spirits protect you!",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "I'll be making you an Animal-Mushroom stew, please gather the following ingredients found around the area:\r\n" +
                    "#i4000009# #r10 #b#t4000009#\r\n" +
                    "#i4000001# #r10 #b#t4000001#\r\n" +
                    "#i4000253# #r10 #b#t4000253#\r\n" +
                    "#i4000252# #r5 #b#t4000252#\r\n" +
                    "#i4000017# #r3 #b#t4000017#\r\n" +
                    "#i2010009# #r1 #b#t2010009#",
            ok = { onEnd() }
        )
    }

    override fun finish(c: Client) {
        execute(c, 1072004) {
            with(it) {
                sendMessage(
                    "I see you've gotten the ingredients, I'll be making the stew now!",
                    next = { EatingStew() }
                )
            }
        }
    }

    private fun DialogContext.EatingStew() {
        sendMessage(
            "That was delicious! I feel a lot stronger!",
            next = { completeQuest()},
            speaker = SpeakerType.NpcReplacedByUser
        )
    }

    private fun DialogContext.completeQuest() {
        postRewards(
            listOf(
                ExpQuestReward(1000),
                    ),
                    "Of course, that's the power of a good stew.",
                    take = mapOf(
                        4000009 to 10,
                        4000001 to 10,
                        4000252 to 5,
                        4000253 to 10,
                        2010009 to 1,
                        4000017 to 3),
                )
            }
        }