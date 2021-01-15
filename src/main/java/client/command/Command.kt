package client.command

import client.Character
import client.messages.broadcast.types.NoticeWithoutPrefixMessage
import net.maple.packets.CharacterPackets.message

interface Command {

    val description: String
    fun loadParams(params: Map<Int, String>) {}
    fun execute(chr: Character)

    fun Character.displaySyntax() {
        message(NoticeWithoutPrefixMessage("[COMMAND] Syntax error"))
        message(NoticeWithoutPrefixMessage("Description: $description"))
    }
}