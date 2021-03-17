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

import util.HexTool.toBytes
import java.awt.Point
import java.nio.charset.StandardCharsets

/**
 * @author Brent
 * @author Chronos (Kotlin conversion)
 */
abstract class Writer {

    abstract fun write(b: Int): Writer
    private fun write(lb: Long): Writer {
        return write(lb.toInt())
    }

    fun write(byteArr: ByteArray, off: Int = 0, len: Int = byteArr.size): Writer {
        for (i in off until len) {
            write(byteArr[i].toInt())
        }
        return this
    }

    fun write(vararg b: Int): Writer {
        for (value in b) {
            write(value)
        }
        return this
    }

    fun writeByte(b: Byte): Writer {
        return write(b.toInt())
    }

    fun writeShort(s: Int): Writer {
        return write(s and 0xFF).write(s ushr 8)
    }

    fun writeShort(s: Short): Writer {
        return write(s.toInt() and 0xFF).write(s.toInt() ushr 8)
    }

    fun writeChar(c: Char): Writer {
        return writeShort(c.toInt())
    }

    fun writeInt(i: Int): Writer {
        return write(i and 0xFF).write(i ushr 8)
                .write(i ushr 16).write(i ushr 24)
    }

    fun writeInt(i: IntegerValue): Writer {
        return writeInt(i.value)
    }

    fun writeFloat(f: Float): Writer {
        return writeInt(java.lang.Float.floatToIntBits(f))
    }

    fun writeLong(i: Int): Writer {
        return writeLong(i.toLong())
    }

    fun writeLong(l: Long): Writer {
        return write(l and 0xFF).write(l ushr 8)
                .write(l ushr 16).write(l ushr 24)
                .write(l ushr 32).write(l ushr 40)
                .write(l ushr 48).write(l ushr 56)
    }

    fun writeDouble(d: Double): Writer {
        return writeLong(java.lang.Double.doubleToLongBits(d))
    }

    private fun writeFixedString(s: String): Writer {
        return write(s.toByteArray(ASCII))
    }

    fun writeFixedString(s: String, size: Int): Writer {
        write(s.toByteArray(ASCII))
        for (i in 0 until size - s.length) {
            write(0x00)
        }
        return this
    }

    fun writeMapleString(s: String): Writer {
        return writeShort(s.length).writeFixedString(s)
    }

    fun writeNullTerminatedString(s: String): Writer {
        return writeFixedString(s).write(0)
    }

    fun writeHex(s: String): Writer {
        return write(toBytes(s))
    }

    fun writeBool(b: Boolean): Writer {
        return write(if (b) 1 else 0)
    }

    fun writePosition(p: Point): Writer {
        return writeShort(p.x).writeShort(p.y)
    }

    abstract val offset: Int

    abstract fun close()

    companion object {
        private val ASCII = StandardCharsets.US_ASCII
    }
}