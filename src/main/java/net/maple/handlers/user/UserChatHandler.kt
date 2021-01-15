package net.maple.handlers.user

import client.Character
import client.Client
import client.command.CommandHandler
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter
import javax.script.ScriptEngine
import javax.script.ScriptException

class UserChatHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        reader.readInteger() // ?

        val msg = reader.readMapleString()
        val textBox = !reader.readBool()

        if ((msg.startsWith("!") && chr.isGM) || msg.startsWith("@")) {
            CommandHandler.executeCommand(chr, msg)
            return
        }

        chr.field.broadcast(sendMessage(chr, msg, textBox), null)
    }

    companion object {
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