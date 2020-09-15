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

import util.HexTool.toHex

/**
 * Represents a packet to be sent over a TCP socket for MapleStory.
 * Very simply, it is an abstraction of raw data that applies some extra
 * functionality because it is a MapleStory packet.
 *
 * @author Brent
 * @author Chronos (Kotlin conversion)
 */
class Packet(val data: ByteArray) : Cloneable {

    val length: Int get() = data.size
    val header: Int
        get() {
            return if (data.size < 2) {
                0xFFFF
            } else (data[0] + (data[1].toInt() shl 8))
        }

    override fun toString(): String {
        return toHex(data)
    }

    public override fun clone(): Packet {
        val len = length
        val data = ByteArray(len)
        System.arraycopy(this.data, 0, data, 0, len)
        return Packet(data)
    }
}