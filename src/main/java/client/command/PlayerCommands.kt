package client.command

import client.Character
import client.messages.broadcast.types.AlertMessage
import net.maple.packets.CharacterPackets.message

class PlayerCommands {

    object Dispose : Command {

        override val description: String = "@dispose - attempt to unstuck the player"

        override fun execute(chr: Character) {
            chr.enableActions()
            chr.message(AlertMessage("You've been disposed!"))
        }
    }

    object Unstuck : Command by Dispose
}