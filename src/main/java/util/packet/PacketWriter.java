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
package util.packet;

import constants.ServerConstants;
import net.maple.SendOpcode;
import util.HexTool;

import java.util.Arrays;

/**
 * Artifact from Invictus. Modified because this is relatively cheap enough
 * and the addition of locks and keeping one for a session was probably overkill
 * for something this simple.
 *
 * @author Brent
 */
public final class PacketWriter extends Writer {

    private static int[] ignoreOps = {
            SendOpcode.PING.getValue(),
            SendOpcode.USER_MOVE.getValue(),
            SendOpcode.NPC_CHANGE_CONTROLLER.getValue(),
            SendOpcode.NPC_ENTER_FIELD.getValue(),
            SendOpcode.NPC_MOVE.getValue()
    };
    private int offset;
    private byte[] data;

    public PacketWriter(int size) {
        offset = 0;
        data = new byte[size];
    }

    private void expand(int size) {
        byte[] nd = new byte[size];
        System.arraycopy(data, 0, nd, 0, offset);
        data = nd;
    }

    private void trim() {
        expand(offset);
    }

    @Override
    public final PacketWriter write(int b) {
        if (offset + 1 >= data.length) {
            expand(data.length * 2);
        }
        data[offset++] = (byte) b;
        return this;
    }

    public final Writer writeHeader(IntegerValue i) {
        int opCode = i.getValue();
        String hex = Integer.toHexString(opCode);
        if (ServerConstants.LOG && Arrays.stream(ignoreOps).noneMatch(p -> p == opCode))
            System.out.println("[SEND] packet " + opCode + " (" + (hex.length() == 1 ? "0x0" : "0x") + hex.toUpperCase() + ") - " + SendOpcode.getEnumByString(opCode));
        return writeShort(i.getValue());
    }

    public final Writer writeShort(IntegerValue s) {
        return writeShort(s.getValue());
    }

    @Override
    public final int getOffset() {
        return offset;
    }

    public final byte[] getData() {
        return data;
    }

    @Override
    public final void close() {
        offset = -1;
        data = null;
    }

    @Override
    public final String toString() {
        return HexTool.toHex(data);
    }

    public final byte[] data() {
        if (data != null) {
            if (data.length > offset) {
                trim();
            }
            return data;
        }
        return null;
    }

    public final Packet createPacket() {
        if (data != null) {
            if (data.length > offset) {
                trim();
            }
            return new Packet(data);
        }
        return null;
    }
}
