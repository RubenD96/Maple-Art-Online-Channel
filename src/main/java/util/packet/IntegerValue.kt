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
 * Artifact from Invictus. Used as a means of storing an integer-typed value
 * as an abstraction of an object or enumerator member. Follows a generic
 * get-set access pattern.
 *
 * @author Brent
 * @author Chronos (Kotlin conversion)
 */
interface IntegerValue {

    /**
     * The value associated with this integer value.
     */
    val value: Int

    /**
     * Makes it possible to use bit operations with this class
     */
    infix fun or(other: IntegerValue): Int {
        return this.value or other.value
    }

    companion object {
        infix fun Int.or(other: IntegerValue): Int {
            return this or other.value
        }
    }
}