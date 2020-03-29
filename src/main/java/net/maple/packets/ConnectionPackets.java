package net.maple.packets;

import constants.ServerConstants;
import net.maple.SendOpcode;
import util.packet.Packet;
import util.packet.PacketWriter;

public class ConnectionPackets {

    /**
     * Sends a hello packet.
     *
     * @param sendIv the IV used by the net.server for sending
     * @param recvIv the IV used by the net.server for receiving
     * @return Hello Packet
     */
    public static Packet sendHello(byte[] sendIv, byte[] recvIv, boolean testServer) {
        PacketWriter pw = new PacketWriter(14);

        pw.writeShort(14);
        pw.writeShort(ServerConstants.VERSION);
        pw.writeMapleString("1");
        pw.write(recvIv);
        pw.write(sendIv);
        pw.write(testServer ? 5 : 8);

        return pw.createPacket();
    }

    /**
     * Sends a ping packet.
     *
     * @return The packet.
     */
    public static Packet getPing() {
        final PacketWriter pw = new PacketWriter(2);

        pw.writeHeader(SendOpcode.PING);

        return pw.createPacket();
    }
}
