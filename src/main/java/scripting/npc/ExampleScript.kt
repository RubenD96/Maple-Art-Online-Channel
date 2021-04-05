package scripting.npc

import client.Client
import scripting.npc.DialogUtils.blue
import scripting.npc.DialogUtils.bold
import scripting.npc.DialogUtils.green
import scripting.npc.DialogUtils.purple
import scripting.npc.DialogUtils.red

@Npc([22000])
class ExampleScript : NPCScript() {

    override fun start(c: Client) {
        execute(c) {
            it.firstDialog()
        }
    }

    private fun DialogContext.firstDialog() {
        sendMessage(
            "Hello!".bold().green(),
            ok = { secondDialog() }
        )
    }

    private fun DialogContext.secondDialog() {
        sendMessage(
            "Bye!".blue(),
            yes = { yesDialog() },
            no = { noDialog() }
        )
    }

    private fun DialogContext.yesDialog() {
        sendMessage(
            "You clicked yes.".red(),
            ok = { firstDialog() }
        )
    }

    private fun DialogContext.noDialog() {
        sendMessage(
            "You clicked no.".purple(),
            ok = { firstDialog() }
        )
    }

    override fun DialogContext.onEnd() {
        endMessage("You clicked end, at any dialog".bold())
    }
}