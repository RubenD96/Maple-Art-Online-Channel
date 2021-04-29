package scripting.dialog

import scripting.Script

abstract class DialogScript : Script<Int> {

    override var value = 0
    val id get() = value

    open fun DialogContext.onEnd() {
        clearStates()
    }

    fun onError() {
        println("Something went wrong, please tell a gm!")
    }
}