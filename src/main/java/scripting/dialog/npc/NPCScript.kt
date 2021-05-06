package scripting.dialog.npc

import client.Client
import scripting.dialog.DialogScript
import scripting.dialog.DialogContext
import java.util.function.Consumer

abstract class NPCScript : DialogScript() {

    fun start(c: Client, consumer: Consumer<DialogContext>) {
        val context = DialogContext(this, c, id)
        c.script = context
        consumer.accept(context)
    }
}