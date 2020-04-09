package client.messages;

import util.packet.PacketWriter;

public interface Message {

    MessageType getType();

    void encode(PacketWriter pw);
}
