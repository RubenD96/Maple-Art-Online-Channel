package client.effects;

import util.packet.PacketWriter;

public interface FieldEffectInterface {

    FieldEffectType getType();

    void encode(PacketWriter pw);
}
