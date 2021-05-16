package scripting.scripts.npc

import client.Client
import scripting.dialog.DialogUtils.openStorage
import scripting.dialog.npc.NPCScript
import scripting.dialog.npc.Npc

@Npc([2070000])
class Storage : NPCScript() {

    override fun execute(c: Client) {
        start(c) {
            c.openStorage()
        }
    }
}