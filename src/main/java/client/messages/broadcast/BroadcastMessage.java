package client.messages.broadcast;

import util.packet.PacketWriter;

public interface BroadcastMessage {

    BroadcastMessageType getType();

    void encode(PacketWriter pw);
}
