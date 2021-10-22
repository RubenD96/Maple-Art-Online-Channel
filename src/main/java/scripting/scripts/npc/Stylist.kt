package scripting.scripts.npc

import client.Client
import scripting.dialog.DialogContext
import scripting.dialog.npc.NPCScript
import scripting.dialog.npc.Npc

@Npc([100])
class Stylist : NPCScript() {

    private val DialogContext.floor: Int get() = c.character.fieldId / 1000

    override fun execute(c: Client) {
        start(c) {
            with (it) {

            }
        }
    }
}