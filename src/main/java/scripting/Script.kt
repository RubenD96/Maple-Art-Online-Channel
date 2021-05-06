package scripting

import client.Client

interface Script<T> {

    var value: T

    fun execute(c: Client)
}