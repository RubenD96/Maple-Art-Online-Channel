package scripting.scripts.npc

import client.Client
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.bold
import scripting.dialog.DialogUtils.letters
import scripting.dialog.DialogUtils.red
import scripting.dialog.npc.NPCScript
import scripting.dialog.npc.Npc

@Npc([9060000])
class WeaponCreator : NPCScript() {

    private var DialogContext.itemId
        get() = holder.numberData["mobid"]!! as Int
        set(value) {
            holder.numberData["mobid"] = value
        }

    override fun execute(c: Client) {
        start(c) {
            with(it) {
                if (!c.isAdmin) {
                    onEnd()
                    return@start
                }

                sendGetNumber(
                    "Stat Editor".letters() + "\r\n\r\n" +
                            "Provide a valid equip id:",
                    min = 1000000,
                    max = 1999999,
                    def = 1302000,
                    positive = { id ->
                        itemId = id
                        showStats()
                    }
                )
            }
        }
    }

    private fun DialogContext.showStats() {

    }

    override fun DialogContext.onEnd() {
        endMessage("Swiggity swooty I'm coming for that booty!".red().bold())
    }
}