package scripting

import client.Client

interface Script {

    fun start(c: Client)
}