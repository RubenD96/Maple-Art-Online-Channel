package net.netty;

import constants.ServerConstants;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.maple.handlers.PacketHandler;
import net.maple.PacketProcessor;
import net.maple.RecvOpcode;
import net.maple.packets.ConnectionPackets;
import player.Client;
import util.crypto.MapleAESOFB;
import util.packet.Packet;
import util.packet.PacketReader;

import java.util.Arrays;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    PacketProcessor processor = PacketProcessor.getInstance();
    int[] ignoreOps = {
            RecvOpcode.PONG.getValue(),
            RecvOpcode.UPDATE_SCREEN_SETTING.getValue()
    };

    @Override
    public void channelActive(ChannelHandlerContext chc) {
        Channel channel = chc.channel();
        byte[] ivSend = {82, 48, 120, (byte) (Math.random() * 255)};
        byte[] ivRecv = {70, 114, 122, (byte) (Math.random() * 255)};

        Client client = new Client(channel, ivSend, ivRecv);

        client.write(ConnectionPackets.sendHello(ivSend, ivRecv, false));

        channel.attr(Client.CLIENT_KEY).set(client);
        channel.attr(Client.CRYPTO_KEY).set(new MapleAESOFB());

        System.out.printf("Opened session with %s%n", client.getIP());

        client.startPing();
    }

    @Override
    public void channelRead(ChannelHandlerContext chc, Object msg) {
        Packet packet = (Packet) msg;
        Channel channel = chc.channel();

        Client client = (Client) channel.attr(Client.CLIENT_KEY).get();
        PacketReader packetReader = client.getReader().next(packet);

        short opCode = packetReader.readShort();
        final PacketHandler packetHandler = processor.getHandler(opCode);

        String hex = Integer.toHexString(opCode);
        if (ServerConstants.LOG && Arrays.stream(ignoreOps).noneMatch(p -> p == opCode)) {
            if (packetHandler == null) {
                System.out.println("[RECEIVED] packet " + opCode + " (" + (hex.length() == 1 ? "0x0" : "0x") + hex.toUpperCase() + ")");
            } else {
                String className = packetHandler.getClass().getName();
                if (!className.contains("DoNothingHandler")) {
                    System.out.println("[RECEIVED] " + className);
                }
            }
            //System.out.printf("data: %s.%n", packet.toString());
        }

        if (packetHandler != null) {
            if (packetHandler.validateState(client)) {
                packetHandler.handlePacket(packetReader, client);
            } else {
                System.out.printf("Client failed to validate state for packet %s.%n", opCode);
                channel.close();
            }
        } else {
            System.out.printf("Received completely unhandled packet %s.%n", packet.toString());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext chc) {
        Channel ch = chc.channel();

        Client c = (Client) ch.attr(Client.CLIENT_KEY).get();

        c.disconnect();
        //c.softDisconnect(c.isLoggedIn()); // handle this is we don't soft disconnect through handler

        c.cancelPingTask();

        // remove after debug stage
        System.out.printf("[Debug] Closed session with %s.%n", c.getIP());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext chc, Throwable cause) {
        // Close the connection when an exception is raised.
        Channel ch = chc.channel();
        Client c = (Client) ch.attr(Client.CLIENT_KEY).get();
        c.disconnect();
        System.out.println(cause.getMessage());
        chc.close();
    }
}