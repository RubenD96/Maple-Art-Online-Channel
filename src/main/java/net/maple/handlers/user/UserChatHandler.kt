package net.maple.handlers.user

import client.Character
import client.Client
import client.messages.broadcast.types.NoticeWithoutPrefixMessage
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine
import constants.ServerConstants.COMMAND_FILE_LIST
import constants.ServerConstants.COMMAND_LIST
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import net.maple.packets.CharacterPackets
import scripting.shortcuts.CommandShortcut
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.script.ScriptEngine
import javax.script.ScriptException

class UserChatHandler : PacketHandler() {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        reader.readInteger() // ?

        val msg = reader.readMapleString()
        val cmd = msg.split(" ".toRegex()).toTypedArray()
        val textBox = !reader.readBool()

        if (COMMAND_LIST[chr.gmLevel].contains(cmd[0].substring(1)) && msg[0] == '!') {
            val engine = c.engines["cmd"] ?: GraalJSScriptEngine.create() ?: return

            try {
                val args = cmd.copyOfRange(1, cmd.size)
                engine.put("cs", CommandShortcut(c, args))

                if (cmd[0].substring(1) == "eval") {
                    engine.eval(msg.substring(6))
                    return
                }

                engine.eval(COMMAND_FILE_LIST[cmd[0].substring(1)])

                if (c.isAdmin) c.write(CharacterPackets.message(NoticeWithoutPrefixMessage("Successfully executed command!")))
            } catch (se: ScriptException) {
                se.printStackTrace()
            }
            return
        }
        chr.field.broadcast(sendMessage(chr, msg, textBox), null)
    }

    private fun eval(c: Client, command: String) {
        val engine: ScriptEngine = GraalJSScriptEngine.create()
        println("Evaluating:\n$command")
        try {
            engine.put("c", c)
            engine.put("chr", c.character)
            engine.put("field", c.character.field)
            engine.eval(command)
        } catch (se: ScriptException) {
            se.printStackTrace()
        }
    }

    companion object {
        fun refreshCommandList() {
            try {
                Files.walk(Paths.get("scripts/command")).use { walk ->
                    walk.filter { Files.isRegularFile(it) }
                            .forEach { x: Path ->
                                val s = x.toString()
                                val trimmed = s.substring(s.indexOf("\\", s.indexOf("\\") + 1) + 1)

                                val level = trimmed.substring(0, trimmed.lastIndexOf('\\'))
                                val command = trimmed.substring(trimmed.lastIndexOf('\\') + 1, trimmed.lastIndexOf('.'))

                                try {
                                    COMMAND_FILE_LIST[command] = String(Files.readAllBytes(x))
                                } catch (ioe: IOException) {
                                    ioe.printStackTrace()
                                }

                                when (level) {
                                    "Admin" -> {
                                        COMMAND_LIST[2].add(command)
                                        COMMAND_LIST[1].add(command)
                                        COMMAND_LIST[0].add(command)
                                    }
                                    "GM" -> {
                                        COMMAND_LIST[1].add(command)
                                        COMMAND_LIST[0].add(command)
                                    }
                                    "Player" -> COMMAND_LIST[0].add(command)
                                }
                            }
                }
            } catch (ioe: IOException) {
                ioe.printStackTrace()
            }
        }

        private fun sendMessage(chr: Character, msg: String, textBox: Boolean): Packet {
            val pw = PacketWriter(16)

            pw.writeHeader(SendOpcode.USER_CHAT)
            pw.writeInt(chr.id)
            pw.writeBool(chr.isGM)
            pw.writeMapleString(msg)
            pw.writeBool(!textBox)

            return pw.createPacket()
        }
    }
}