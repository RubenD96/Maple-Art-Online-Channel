package field.movement.fragment;

import field.life.FieldLife;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public interface MoveFragment {

    void apply(FieldLife life);

    void decode(PacketReader packet);

    void encode(PacketWriter packet);
}
