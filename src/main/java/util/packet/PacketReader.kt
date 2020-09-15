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

/**
 * Artifact from Invictus. Unlike PacketWriter, this is able to be used for
 * reading data from a client over and over again without needing a lock. It is
 * still practical to have on per session rather than creating new generations
 * for each new received packet.
 *
 * @author Brent
 * @author Chronos (Kotlin conversion)
 */
class PacketReader : Reader() {

    override var offset: Int = 0
        private set
    lateinit var data: ByteArray
        private set

    fun next(d: ByteArray): PacketReader {
        offset = 0
        data = d
        return this
    }

    fun next(p: Packet): PacketReader {
        return next(p.data)
    }

    override fun read(): Int {
        return try {
            0xFF and data[offset++].toInt()
        } catch (e: Exception) {
            -1
        }
    }

    override fun skip(num: Int): PacketReader {
        offset += num
        return this
    }

    override fun available(): Int {
        return data.size - offset
    }

    override fun close() {
        offset = -1
    }

    init {
        offset = -1
    }
}