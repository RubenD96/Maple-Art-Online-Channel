package net.maple.packets;

import client.Client;
import constants.ServerConstants;
import database.jooq.tables.Accounts;
import net.database.AccountAPI;
import net.maple.SendOpcode;
import net.server.ChannelServer;
import util.packet.Packet;
import util.packet.PacketWriter;

import java.net.InetAddress;
import java.net.UnknownHostException;

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

    public static Packet getChangeChannelPacket(ChannelServer channel) {
        PacketWriter pw = new PacketWriter(9);

        try {
            pw.writeHeader(SendOpcode.MIGRATE_COMMAND);
            pw.writeBool(true);
            pw.write(InetAddress.getByName(channel.getIP()).getAddress());
            pw.writeShort(channel.getPort());
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        }

        return pw.createPacket();
    }
}
