package scripting.scripts.quest.floor11

import client.Client
import client.player.quest.reward.ExpQuestReward
import scripting.dialog.DialogContext
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScript

@Quest([11006])
class TDSTheWisdomTest11006: QuestScript() {
    override fun execute(c: Client) {
        execute(c, 2081008) {
            with(it) {
                sendMessage(
                "In the wisdom test, you will have to answer 3 questions correctly." +
                    "\r\nAre you ready?",
                    yes = {onAccept()},
                )
            }
        }
    }

    private fun DialogContext.onAccept() {
        startQuest()
        sendMessage(
            "Then let's begin.",
            ok = { onEnd() }
        )
    }

    private fun DialogContext.answerHandler(
        guess: String,
        correctAnswer: String,
        successText: String,
        failText: String,
        nextFunction: () -> Unit) {
        if (guess.toLowerCase() == correctAnswer) {
            sendMessage(
                successText,
                ok = { nextFunction() }
            )
        } else {
            sendMessage(
                failText,
                ok = { onEnd() }
            )
        }
    }

    override fun finish(c: Client) {
        execute(c, 2081008) {
            with(it) {
                firstQuestion()
            }
        }
    }

    private fun DialogContext.firstQuestion() {
        sendGetText(
            "\r\nFor thousands of years," +
                    "\r\nSeen only in tale." +
                    "\r\nThe wind as a sail," +
                    "\r\nFor one thunderous gale." +
                    "\r\nShiny stores rich in lore," +
                    "\r\nThe burning temper, like Earth's core." +
                    "\r\n\r\n What am I?",
            min = 1,
            max= 1000000, //Why are those needed in text?
            positive = {guess ->
                answerHandler(
                    guess,
                    correctAnswer =  "dragon",
                    successText = "Well done, dragon is the correct answer." +
                            "\r\nMoving on to the second question.",
                    failText = "Wrong, You can do better than that." +
                            "\r\nProve that you are the one who can defeat him!",
                    nextFunction = { secondQuestion() }

                )}
        )
    }


    private fun DialogContext.secondQuestion() {
        sendGetText(
                "It cannot be seen, cannot be felt," +
                    "\r\nCannot be heard, cannot be smelt." +
                    "\r\nIt lies behind stars and under hills," +
                    "\r\nAnd empty holes it fills." +
                    "\r\nIt comes out first and follows after," +
                    "\r\nEnd life, kills laughter." +
                    "\r\n\r\nWhat am I?",
            min = 1,
            max= 1000000, //Why are those needed in text?
            positive = {guess ->
                answerHandler(
                    guess,
                    correctAnswer =  "darkness",
                    successText = "Impressive." +
                            "\r\nMoving on to the third and final question.",
                    failText = "That's incorrect." +
                            "\r\nTry harder, the darkness must be released.",
                    nextFunction = { lastQuestion() }

                )}
        )
    }

    private fun DialogContext.lastQuestion() {
        sendGetText(
            "I am everyone, yet no-one." +
                "\r\nI can be anywhere, or nowhere." +
                "\r\nWhen the lights go out I disappear and when I face myself we multiply." +
                "\r\n\r\nWhat am I?",
            min = 1,
            max= 1000000, //Why are those needed in text?
            positive = {guess ->
                answerHandler(
                    guess,
                    correctAnswer =  "reflection",
                    successText = "Correct, you have passed the wisdom test.",
                    failText = "Wrong," +
                            "\r\nReflect on your answers and try again." +
                            "\r\nThis is the last test, do not give up!",
                    nextFunction = { onAllCorrectAnswers() }

                )}
        )
    }

    private fun DialogContext.onAllCorrectAnswers() {
        postRewards(
            listOf(
                ExpQuestReward(60000)
            ),
            "Next up is the strength test, good luck child."
        )
    }
}