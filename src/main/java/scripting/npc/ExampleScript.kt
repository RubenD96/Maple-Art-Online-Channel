package scripting.npc

import client.Client

@Npc([22000])
class ExampleScript : NPCScript() {

    override fun start(c: Client) {
        execute(c) {
            with(it) {
                sendMessage(
                    "Test",
                    ok = { }
                )
            }
        }
    }
}