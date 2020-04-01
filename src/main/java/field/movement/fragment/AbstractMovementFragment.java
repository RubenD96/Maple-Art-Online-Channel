package field.movement.fragment;

import field.object.life.FieldLife;
import lombok.Getter;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public abstract class AbstractMovementFragment implements MoveFragment {

    @Getter final protected byte movePathAttribute;

    public AbstractMovementFragment(byte movePathAttribute, PacketReader packetReader) {
        this.movePathAttribute = movePathAttribute;
        decode(packetReader);
    }

    @Override
    public abstract void apply(FieldLife life);

    @Override
    public void decode(PacketReader packet) {
        decodeData(packet);
    }

    @Override
    public void encode(PacketWriter packet) {
        packet.write(movePathAttribute);
        encodeData(packet);
    }

    public abstract void decodeData(PacketReader packet);

    public abstract void encodeData(PacketWriter packet);
}
