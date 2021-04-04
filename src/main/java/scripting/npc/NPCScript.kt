package scripting.npc

import client.Client
import scripting.Script
import java.util.function.Consumer

abstract class NPCScript : Script {

    var id: Int = 0

    fun execute(c: Client, consumer: Consumer<DialogContext>) {
        val context = DialogContext(this, c, id)
        consumer.accept(context)
    }

    open fun onEnd() {
        println("cm.dispose();")
    }

    fun onError() {
        println("Something went wrong, please tell a gm!")
    }
}