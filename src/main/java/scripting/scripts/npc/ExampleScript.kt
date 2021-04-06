package scripting.scripts.npc

import client.Client
import scripting.npc.DialogContext
import scripting.npc.DialogUtils.blue
import scripting.npc.DialogUtils.bold
import scripting.npc.DialogUtils.green
import scripting.npc.DialogUtils.purple
import scripting.npc.DialogUtils.red
import scripting.npc.NPCScript
import scripting.npc.Npc

@Npc([22000])
class ExampleScript : NPCScript() {

    override fun start(c: Client) {
        execute(c) {
            it.okDialog()
        }
    }

    private fun DialogContext.okDialog() {
        sendMessage(
            "Hello!".bold().green(),
            ok = { yesNoDialog() }
        )
    }

    private fun DialogContext.yesNoDialog() {
        sendMessage(
            "Bye!".blue(),
            yes = { yesDialog() },
            no = { noDialog() }
        )
    }

    private fun DialogContext.yesDialog() {
        sendMessage(
            "You clicked yes.".red(),
            ok = { simpleDialog() }
        )
    }

    private fun DialogContext.noDialog() {
        sendMessage(
            "You clicked no.".purple(),
            ok = { numberDialog() }
        )
    }

    private fun DialogContext.simpleDialog() {
        sendSimple(
            "Click an option!",
            appendText = "ok bye",
            selections = linkedMapOf(
                "Go to yes dialog".blue() to { yesDialog() },
                "Go to no dialog".blue() to { noDialog() }
            )
        )
    }

    private fun DialogContext.numberDialog() {
        sendGetNumber(
            "What number am I thinking of??",
            0,
            0,
            100,
            {
                sendMessage(
                    "Your input was: $it",
                    ok = { textDialog() }
                )
            }
        )
    }

    private fun DialogContext.textDialog() {
        sendGetText(
            "Type a sentence... i guess",
            0,
            20,
            "Hello?",
            positive = {
                sendMessage(
                    "Your input was: ${it.red()}",
                    ok = { okDialog() }
                )
            }
        )
    }

    override fun DialogContext.onEnd() {
        endMessage("You clicked end, at any dialog".bold())
    }
}