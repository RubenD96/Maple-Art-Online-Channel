package scripting.dialog.npc

import client.Client
import scripting.DialogScript
import scripting.dialog.DialogContext
import java.util.function.Consumer

abstract class NPCScript : DialogScript() {

    fun execute(c: Client, consumer: Consumer<DialogContext>) {
        val context = DialogContext(this, c, id)
        c.script = context
        consumer.accept(context)
    }
}