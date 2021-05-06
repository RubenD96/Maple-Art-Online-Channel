package scripting.scripts.npc

import client.Client
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.bold
import scripting.dialog.DialogUtils.green
import scripting.dialog.DialogUtils.purple
import scripting.dialog.DialogUtils.red
import scripting.dialog.StateHolder
import scripting.dialog.npc.NPCScript
import scripting.dialog.npc.Npc

@Npc([22000])
class ExampleNpc : NPCScript() {

    var i: Int = 0

    class Data : StateHolder() {
        var input = "TODO"
    }

    override fun execute(c: Client) {
        start(c) {
            it.holder = Data()
            it.okDialog()
        }
    }

    private fun DialogContext.yikes() {
        sendMessage(
            "Hello",
            ok = {
                sendMessage(
                    "Hello",
                    next = {
                        sendMessage(
                            "Hello",
                            prev = { sendMessage("Hello", ok = { sendMessage("bye", ok = { onEnd() }) }) })
                    })
            }
        )
    }

    private fun DialogContext.okDialog() {
        i++
        holder.numberData["test"] = 3
        sendMessage(
            "Hello!\r\n${(holder as Data).input}\r\nYou've been here $i times".bold().green(),
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
                (holder as Data).input = it
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