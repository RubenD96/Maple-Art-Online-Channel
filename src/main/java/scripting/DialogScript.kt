package scripting

import scripting.dialog.DialogContext

abstract class DialogScript : Script {

    var id: Int = 0

    open fun DialogContext.onEnd() {
        clearStates()
    }

    fun onError() {
        println("Something went wrong, please tell a gm!")
    }
}