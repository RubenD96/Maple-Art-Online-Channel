package net.maple.handlers.user

import client.Client
import net.maple.handlers.PacketHandler
import scripting.dialog.ConversationType
import util.HexTool.toHex
import util.logging.LogType
import util.logging.Logger
import util.packet.PacketReader

class UserScriptMessageAnswerHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        println("[UserScriptMessageAnswerHandler] " + toHex(reader.data))

        val type = reader.readByte()
        val action = reader.readByte()

        c.script?.let {
            with(it.script) {
                when {
                    type.toInt() == ConversationType.ASK_MENU.value -> {
                        val selection = reader.readInteger()

                        when (action.toInt()) {
                            0 -> it.neutral?.invoke() ?: it.onEnd()
                            1 -> it.selections[selection]?.invoke(selection) ?: it.onEnd()
                            else -> it.clearStates()
                        }
                    }
                    type.toInt() == ConversationType.ASK_NUMBER.value -> {
                        val input = reader.readInteger()
                        if (input < it.min || input > it.max) {
                            return run {
                                c.close()
                                Logger.log(LogType.INVALID, "Input $input not allowed on $id", this, c)
                            }
                        }
                        when (action.toInt()) {
                            0 -> it.neutral?.invoke() ?: it.onEnd()
                            1 -> it.positiveWithNumber?.invoke(input) ?: it.onEnd()
                            else -> it.clearStates()
                        }
                    }
                    type.toInt() == ConversationType.ASK_TEXT.value || type.toInt() == ConversationType.ASK_BOX_TEXT.value -> {
                        val text = reader.readMapleString()
                        if (text.length < it.min || text.length > it.max) {
                            return run {
                                c.close()
                                Logger.log(LogType.INVALID, "Text size ${text.length} not allowed on $id", this, c)
                            }
                        }
                        when (action.toInt()) {
                            0 -> it.neutral?.invoke() ?: it.onEnd()
                            1 -> it.positiveWithText?.invoke(text) ?: it.onEnd()
                            else -> it.clearStates()
                        }
                    }
                    else -> {
                        when (action.toInt()) {
                            -1 -> it.negative?.invoke() ?: it.onEnd()
                            0 -> it.neutral?.invoke() ?: it.onEnd()
                            1 -> it.positive?.invoke() ?: it.onEnd()
                            else -> it.clearStates()
                        }
                    }
                }
            }
        }
    }
}