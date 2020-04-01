package field.movement.fragment;

import field.object.life.FieldLife;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public interface MoveFragment {

    void apply(FieldLife life);

    void decode(PacketReader packet);

    void encode(PacketWriter packet);
}
