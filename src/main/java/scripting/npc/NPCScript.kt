package scripting.npc

import client.Client
import scripting.Script
import java.util.function.Consumer

abstract class NPCScript : Script {

    var id: Int = 0

    fun execute(c: Client, consumer: Consumer<DialogContext>) {
        val context = DialogContext(this, c, id)
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