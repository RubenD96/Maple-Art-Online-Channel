package field.movement.fragment;

import field.life.FieldLife;
import lombok.Getter;
import lombok.Setter;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class StatChangeMoveFragment extends AbstractMovementFragment {

    @Getter @Setter boolean stat;

    public StatChangeMoveFragment(byte movePathAttribute, PacketReader packetReader) {
        super(movePathAttribute, packetReader);
    }

    @Override
    public void apply(FieldLife life) {

    }

    @Override
    public void decodeData(PacketReader packet) {
        stat = packet.readBool();
    }

    @Override
    public void encodeData(PacketWriter packet) {
        packet.writeBool(stat);
    }
}
