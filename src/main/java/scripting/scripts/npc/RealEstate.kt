package scripting.scripts.npc

import client.Client
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.bold
import scripting.dialog.DialogUtils.letters
import scripting.dialog.DialogUtils.purple
import scripting.dialog.DialogUtils.red
import scripting.dialog.npc.NPCScript
import scripting.dialog.npc.Npc

@Npc([9000036])
class RealEstate : NPCScript() {

    private val playerName = DialogUtils.playerName.purple()

    override fun execute(c: Client) {
        start(c) {
            it.sendMessage(
                "Hey there $playerName, I'm ${"Claire".blue()} and I'm the ${"best".red()} real estate agent in this world!",
                next = { it.mainMenu() }
            )
        }
    }

    private fun DialogContext.mainMenu() {
        sendSimple(
            "Real Estate".letters() + "\r\n\r\n" +
                    "Ask me anything about real estate!",
            selections = linkedMapOf(
                "What can I do with a house in general?".blue() to { generalInfo() },
                "What does the house in this town offer?".blue() to {},
                "What are the costs of this house?\r\n".blue() to {},
                "I would like to buy the house for this town!".red() to {}
            )
        )
    }

    private fun DialogContext.generalInfo() {
        sendMessage("Oh $playerName! You should've asked what you ${"can't".red()} do with houses!\r\n\r\n" +
                "A house is a space for ${"yourself".blue()}, " +
                "unless you want to have some ${"friends".purple()} over!\r\n" +
                "Every town offers a different house, " +
                "houses on ${"higher floors".red()} are usually ${"better".blue().bold()}!\r\n\r\n" +
                "Besides the ${"aesthetics and status".blue()} of owning a house, " +
                "a house can have several functional purposes\r\n" +
                "At certain houses you'll may find ${"npc's".blue()} that " +
                "${"sell items".purple()}, ${"give quests".purple()} or " +
                "do something truly ${"unique".red().bold()}!\r\n\r\n" +
                "A guildhouse is a special kind of real estate; " +
                "every single member of the guild has access to it once it has been purchased.\r\n\r\n" +
                "Every house comes at a price, the ${"higher".red()} up the house, " +
                "the ${"higher".red()} the ${"price".blue()}!\r\n",
            ok = { mainMenu() }
        )
    }
}