package client.command

import client.Character
import client.messages.broadcast.types.NoticeWithoutPrefixMessage
import constants.ServerConstants
import net.maple.packets.CharacterPackets.message
import util.logging.LogType
import util.logging.Logger

object CommandHandler {

    private val commands: MutableMap<String, Command> = LinkedHashMap()

    /**
     * Load all commands, should only be used once!
     */
    fun loadCommands() {
        commands.clear() // for idiots

        PlayerCommands::class.nestedClasses.forEach {
            commands["@" + it.simpleName!!.toLowerCase()] = it.objectInstance as Command
        }

        GMCommands::class.nestedClasses.forEach {
            commands["!" + it.simpleName!!.toLowerCase()] = it.objectInstance as Command
        }
    }

    /**
     * Execute a command
     *
     * @param chr The character that triggered it
     * @param str The complete message string, including prefix
     */
    fun executeCommand(chr: Character, str: String) {
        commands[str.substringBefore(" ").toLowerCase()]?.let {
            Logger.log(LogType.COMMAND, str, this, chr.client)
            synchronized(commands) {
                try {
                    val params = if (str.contains(" ")) str.substringAfter(" ").getParams() else mapOf()
                    it.loadParams(params)
                    it.execute(chr)
                } catch (e: Exception) {
                    with(it) {
                        chr.displaySyntax()
                    }
                    if (ServerConstants.DEBUG) {
                        e.printStackTrace()
                    }
                }
            }
        } ?: run {
            chr.message(NoticeWithoutPrefixMessage("Command ${str.substringBefore(" ")} does not exist"))
        }
    }

    /**
     * Turns a complete text string into a map of parameters
     * We use a map instead of a list here to properly check for NPE's in Command.loadParams
     */
    private fun String.getParams(): Map<Int, String> {
        val params = HashMap<Int, String>()
        split(" ").forEach {
            params[split(" ").indexOf(it)] = it
        }
        return params
    }

    /**
     * Gets a list of all commands
     *
     * @param desc if true, gets a list of all descriptions, as opposed to just the commands
     */
    fun getAllCommands(desc: Boolean): List<String> {
        return if (desc) {
            val list = ArrayList<String>()
            commands.forEach {
                list.add(it.value.description)
            }
            list
        } else {
            commands.keys.toList()
        }
    }
}