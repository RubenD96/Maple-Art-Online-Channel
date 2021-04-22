package scripting.quest

import client.Client
import scripting.npc.DialogContext
import scripting.npc.NPCScript
import java.util.function.Consumer

abstract class QuestScript : NPCScript() {

    override fun execute(c: Client, npc: Int, consumer: Consumer<DialogContext>) {
        super.execute(c, npc, consumer)
    }

    abstract fun finish(c: Client)
}