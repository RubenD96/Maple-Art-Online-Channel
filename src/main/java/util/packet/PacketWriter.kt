/*
    This file is part of Desu: MapleStory v62 net.server.Server Emulator
    Copyright (C) 2017  Brenterino <therealspookster@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package util.packet

import constants.ServerConstants
import net.maple.SendOpcode
import net.netty.central.CentralSendOpcode
import util.HexTool.toHex

/**
 * Artifact from Invictus. Modified because this is relatively cheap enough
 * and the addition of locks and keeping one for a session was probably overkill
 * for something this simple.
 *
 * @author Brent
 * @author Chronos (Kotlin conversion)
 */
class PacketWriter(size: Int) : Writer() {

    override var offset = 0
        private set
    var data: ByteArray
        private set

    private fun expand(size: Int) {
        val nd = ByteArray(size)
        System.arraycopy(data, 0, nd, 0, offset)
        data = nd
    }

    private fun trim() {
        expand(offset)
    }

    override fun write(b: Int): PacketWriter {
        if (offset + 1 >= data.size) {
            expand(data.size * 2)
        }
        data[offset++] = b.toByte()
        return this
    }

    fun writeMapleHeader(i: Short): Writer {
        val hex = Integer.toHexString(i.toInt())
        if (ServerConstants.LOG && !ignoreOps.contains(i.toInt()))
            println("[MAPLE][SEND] packet " + i + " (" + (if (hex.length == 1) "0x0" else "0x") + hex.toUpperCase() + ") - " + SendOpcode.getStringByCode(i.toInt()))
        return writeShort(i)
    }

    private fun writeCentralHeader(i: Short): Writer {
        val hex = Integer.toHexString(i.toInt())
        if (ServerConstants.LOG)
            println("[CENTRAL][SEND] packet " + i + " (" + (if (hex.length == 1) "0x0" else "0x") + hex.toUpperCase() + ") - " + CentralSendOpcode.getStringByCode(i.toInt()))
        return writeShort(i)
    }

    fun writeHeader(op: SendOpcode): Writer {
        return writeMapleHeader(op.value.toShort())
    }

    fun writeHeader(op: CentralSendOpcode): Writer {
        return writeCentralHeader(op.value.toShort())
    }

    fun writeShort(s: IntegerValue): Writer {
        return writeShort(s.value)
    }

    override fun close() {
        offset = -1
    }

    override fun toString(): String {
        return toHex(data)
    }

    fun data(): ByteArray {
        if (data.size > offset) {
            trim()
        }
        return data
    }

    fun createPacket(): Packet {
        if (data.size > offset) {
            trim()
        }
        return Packet(data)

    }

    companion object {
        private val ignoreOps = intArrayOf(
                SendOpcode.PING.value,
                SendOpcode.USER_MOVE.value,
                SendOpcode.NPC_CHANGE_CONTROLLER.value,
                SendOpcode.NPC_ENTER_FIELD.value,
                SendOpcode.NPC_MOVE.value,
                SendOpcode.MOB_MOVE.value,
                SendOpcode.MOB_CTRL_ACK.value,
                SendOpcode.MOB_ENTER_FIELD.value,
                SendOpcode.MOB_CHANGE_CONTROLLER.value
        )
    }

    init {
        data = ByteArray(size)
    }
}