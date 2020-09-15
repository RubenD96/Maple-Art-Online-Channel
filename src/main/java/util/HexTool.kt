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
package util

/**
 * Artifact from Invictus. Documentation is not going to be added since
 * the logic is fairly straightforward.
 *
 * @author OdinMS (original code)
 * @author Brent (modified code)
 * @author Chronos (kotlin conversion)
 */
object HexTool {

    private val HEX = charArrayOf(
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F'
    )

    fun toHex(b: Byte): String {
        return HEX[b.toInt() shl 8 shr 12 and 0x0F].toString() +
                HEX[b.toInt() shl 8 shr 8 and 0x0F]
    }

    fun toHex(arr: ByteArray): String {
        val ret = StringBuilder()
        for (b in arr) {
            ret.append(toHex(b))
            ret.append(' ')
        }
        return ret.substring(0, ret.length - 1)
    }

    fun toBytes(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4)
                    + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }
}