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

import java.awt.Point
import java.awt.Rectangle
import java.nio.charset.StandardCharsets

/**
 * @author Brent
 * @author Chronos (Kotlin conversion)
 */
abstract class Reader {

    abstract fun read(): Int

    @JvmOverloads
    fun read(byteArr: ByteArray, off: Int = 0, len: Int = byteArr.size) {
        for (i in off until len) {
            byteArr[i] = readByte()
        }
    }

    fun read(num: Int): ByteArray {
        val ret = ByteArray(num)
        for (i in 0 until num) {
            ret[i] = readByte()
        }
        return ret
    }

    fun readBool(): Boolean {
        return read() > 0
    }

    fun readByte(): Byte {
        return read().toByte()
    }

    fun readShort(): Short {
        return (read() + (read() shl 8)).toShort()
    }

    fun readChar(): Char {
        return (read() + (read() shl 8)).toChar()
    }

    fun readInteger(): Int {
        return (read() + (read() shl 8) + (read() shl 16)
                + (read() shl 24))
    }

    fun readRectangle(): Rectangle {
        return Rectangle(readInteger(), readInteger(), readInteger(), readInteger())
    }

    fun readFloat(): Float {
        return java.lang.Float.intBitsToFloat(readInteger())
    }

    fun readLong(): Long {
        return (read() + (read() shl 8) + (read() shl 16)
                + (read() shl 24) + (read() shl 32)
                + (read() shl 40) + (read() shl 48)
                + (read() shl 56)).toLong()
    }

    fun readPoint(): Point {
        return Point(readShort().toInt(), readShort().toInt())
    }

    fun readDouble(): Double {
        return java.lang.Double.longBitsToDouble(readLong())
    }

    fun readString(len: Int): String {
        val sd = ByteArray(len)
        for (i in 0 until len) {
            sd[i] = readByte()
        }
        return String(sd, ASCII)
    }

    fun readMapleString(): String {
        return readString(readShort().toInt())
    }

    fun readNullTerminatedString(): String {
        val sb = StringBuilder()
        var c: Char
        while (((read().toChar()).also { c = it }).toInt() != 0) {
            sb.append(c)
        }
        return sb.toString()
    }

    abstract fun skip(num: Int): Reader
    abstract fun available(): Int
    abstract val offset: Int

    abstract fun close()

    companion object {
        private val ASCII = StandardCharsets.US_ASCII
    }
}