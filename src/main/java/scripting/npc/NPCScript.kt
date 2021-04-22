package scripting.npc

import client.Client
import scripting.Script
import java.util.function.Consumer

abstract class NPCScript : Script {

    var id: Int = 0

    open fun execute(c: Client, npc: Int = id, consumer: Consumer<DialogContext>) {
        val context = DialogContext(this, c, npc)
        c.script = context
        consumer.accept(context)
    }

    open fun DialogContext.onEnd() {
        clearStates()
    }

    fun onError() {
        println("Something went wrong, please tell a gm!")
    }
}