package client.effects;

import util.packet.PacketWriter;

public interface EffectInterface {

    EffectType getType();

    void encode(PacketWriter pw);
}
