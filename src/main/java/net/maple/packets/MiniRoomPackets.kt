package net.maple.packets

import client.Character
import net.maple.SendOpcode
import util.packet.PacketWriter

object MiniRoomPackets {

    /**
     * struct GW_MiniGameRecord
    {
    int nGameID;
    int nWin;
    int nDraw;
    int nLose;
    int nScore;
    };
     */

    fun startMiniRoom() {

    }

    fun Character.setMiniRoomBalloon(sn: Int, title: String, private: Boolean, gameKind: Byte, curUsers: Byte, maxUsers: Byte, gameOn: Boolean) {
        val pw = PacketWriter(8)

        pw.writeHeader(SendOpcode.USER_MINI_ROOM_BALLOON)
        pw.writeInt(id)
        pw.writeBool(true) // enable

        pw.writeInt(sn)
        pw.writeMapleString(title)
        pw.writeBool(private)
        pw.writeByte(gameKind)
        pw.writeByte(curUsers)
        pw.writeByte(maxUsers)
        pw.writeBool(gameOn)

        field.broadcast(pw.createPacket())
    }
}