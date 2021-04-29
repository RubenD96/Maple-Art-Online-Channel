package scripting.dialog.quest

import client.Client
import scripting.dialog.DialogScript
import scripting.dialog.DialogContext
import java.util.function.Consumer

abstract class QuestScript : DialogScript() {

    fun execute(c: Client, npc: Int, consumer: Consumer<DialogContext>) {
        val context = DialogContext(this, c, npc)
        c.script = context
        consumer.accept(context)
    }

    abstract fun finish(c: Client)
}