package scripting.scripts.quest.floor1

import client.Client
import client.player.quest.reward.ExpQuestReward
import client.player.quest.reward.ItemQuestReward
import client.player.quest.reward.MesoQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.bold
import scripting.dialog.DialogUtils.red
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([1003])
class GagasMonsterResearchPart2 : QuestScript() {
    override fun execute(c: Client) {
        execute(c, 9000021) {
            with(it) {
                sendMessage(
                    "Oh no...",
                    next = {firstOhNo()}
                )
            }
        }
    }

    private fun DialogContext.firstOhNo() {
        sendMessage(
            "Oh no!!",
            next = {secondOhNo()}
        )
    }

    private fun DialogContext.secondOhNo() {
        sendMessage(
            "Oh no!!!".bold().red(),
            accept = {onAccept()},
            decline = {onDecline()}
        )
    }

    private fun DialogContext.onAccept() {
        startQuest();
        sendMessage(
            "My research uncovered a hidden location somewhere on Floor 1...",
            next = {postAcceptDialog()}
        )
    }

    private fun DialogContext.onDecline() {
        sendMessage(
            "It'll be the end of the world...",
            ok = {onEnd()}
        )
    }

    private fun DialogContext.postAcceptDialog() {
        sendMessage(
            "There seems to be a very ${"dangerous".red().bold()} mob in that map, please take care of it before it reaches this town!",
            ok = {onEnd()}
        )
    }

    override fun finish(c: Client) {
        execute(c, 9000021) {
            with(it) {
                sendMessage(
                    "Wow, you've saved this town!" +
                            "\r\nYou're a ${"hero".blue()}!!",
                    next = { completeQuest() }
                )
            }
        }
    }
    private fun DialogContext.completeQuest() {
            postRewards(
                listOf(
                    ExpQuestReward(2000),
                    MesoQuestReward(5000),
                    ItemQuestReward(2022248, 10)
                ),
                "Thank you for your help! Here is your reward.",
            )
        }
    }