package scripting.scripts.npc

import client.Client
import net.maple.packets.GuildPackets
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.red
import scripting.dialog.npc.NPCScript
import scripting.dialog.npc.Npc

@Npc([2010008])
class GuildEmblemCreator : NPCScript() {

    companion object {
        const val cost = 5000000
    }

    override fun execute(c: Client) {
        start(c) {
            with(it) {
                c.character.guild?.let {
                    if (it.leader == c.character.id) {
                        sendMessage(
                            "Creating or changing a ${"guild emblem".red()} costs ${"$cost col".blue()}, are you sure you want to continue?",
                            yes = { GuildPackets.inputMark(c.character) }
                        )
                        return@with
                    }
                }

                sendMessage(
                    "You must be the Guild Leader to change the Emblem. Please tell your leader to speak with me.",
                    ok = { onEnd() }
                )
            }
        }
    }
}