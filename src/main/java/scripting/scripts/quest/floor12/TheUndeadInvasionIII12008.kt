package scripting.scripts.quest.floor12

import client.Client
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.playerName
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript
import javax.print.attribute.standard.DialogOwner

@Quest([12008])
class TheUndeadInvasionIII12008: QuestScript() {
    override fun DialogContext.onEnd() {
        endMessage(
            "The whole Sunken City is on the line here!"
        )
    }

    override fun execute(c: Client) {
        execute(c, 9201096) {
            with(it) {
                sendMessage(
                    "Hey $playerName!" +
                    "\r\nYou won't believe we have found!",
                    ok = { explanationOfTheBoneFishSolution() }
                )
            }
        }
    }

    private fun DialogContext.explanationOfTheBoneFishSolution() {
        sendMessage(
            "Books claim that \"the nature of the fish made of bone lies on their bone structure\"" +
            "\r\nApparently, old adventurers used the very same bones ground into powder." +
            "\r\nthat powder, laid down with some other simple ingredients, can completely shatter their structure.",
            ok = { finalDialogue() }
        )
    }

    private fun DialogContext.finalDialogue() {
        sendMessage(
            "I'll go get the rest of the ingredients, and I'll need about 200 powder jars to completely cover the cave's entrance." +
            "\r\nI'm not going to panic again, but the safety of The Sunken City really depends on us, " +
            "\r\nYou have done so much for us already yet I have to ask, can you help one last time?",
            ok = { onAccept() }
        )
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "OH MY GOD THANK YOU SO MUCH I WAS SURE YOU WE'RE GOING TO DECLINE AND THEN WE WERE ALL GOING..." +
            "\r\n*Uhm* I'm sorry, thank you thank you thank you!" +
            "\r\nI'll prepare the cave's entrance, when you finish collecting 200 powder jars, drop them at the cave's entrance, that should do it."
        )
    }

    override fun finish(c: Client) {
        TODO("Not yet implemented")
    }
}