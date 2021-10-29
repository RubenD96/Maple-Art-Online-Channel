package scripting.scripts.npc

import client.Client
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.letters
import scripting.dialog.DialogUtils.red
import scripting.dialog.DialogUtils.skillName
import scripting.dialog.npc.NPCScript
import scripting.dialog.npc.Npc

@Npc([1072003])
class AgilityMaster : NPCScript() {

    override fun execute(c: Client) {
        start(c) {
            with(it) {
                sendMessage(
                    "I can tell you all about the ${"agility".blue()} life skill!",
                    next = { mainMenu() }
                )
            }
        }
    }

    private fun DialogContext.mainMenu() {
        sendSimple(
            "Agility".letters() + "\r\n\r\n" +
                    "Hello there, you seem to move as slow as a snail!",
            selections = linkedMapOf(
                "What is a life skill?".blue() to { lifeSkillExplanation() },
                "What can I do with agility?".blue() to { agilityExplanation() },
                "How do I train agility?".blue() to { trainingExplanation() },
                "Can you show me the rankings for the nearby course?".blue() to { sendMessage("TODO", ok = { onEnd() }) },
                "Can you show me my current progress?".blue() to { sendMessage("TODO", ok = { onEnd() }) }
            )
        )
    }

    private fun DialogContext.lifeSkillExplanation() {
        sendMessage(
            "In this world, there are multiple different life skills. A life skill requires special training." +
                    "\r\n\r\nDepending on the life skill, you can use it to prepare for battle or use it during battles!" +
                    "\r\n\r\nThere is always a master that can teach a life skill, and I'm the official agility master!",
            ok = { mainMenu() }
        )
    }

    private fun DialogContext.agilityExplanation() {
        sendMessage(
            "The higher your agility skill, the faster you get!" +
                    "\r\n\r\nFor each level you gain in the agility life skill, you gain 1 level in the " +
                    "${1002.skillName().red()} skill located in the mastery tab of your skill book." +
                    "\r\n\r\nEach level gives 1% extra speed, every 2 levels gives 1% jump height!",
            ok = { mainMenu() }
        )
    }

    private fun DialogContext.trainingExplanation() {
        var message = "You mainly gain EXP in agility by completing jump quests." +
                "\r\nEach time you complete a jump quest you gain a little EXP. The harder the jump quest, the bigger the reward." +
                "\r\n\r\nThe jump quests are scattered all over ${"Aincrad".blue()}.";
        if (c.character.fieldId == 1500) {
            message += " The very first one is right next to me!"
        }
        message += "\r\n\r\nThere are other ways to gain EXP as well, some party quests may give an EXP reward. Normal quests may also give life skill EXP."
        sendMessage(
            message,
            ok = { mainMenu() }
        )
    }

    override fun DialogContext.onEnd() {
        endMessage("Catch you on the flip flop.")
    }
}