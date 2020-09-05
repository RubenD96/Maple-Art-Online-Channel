package field.movement.fragment;

import field.movement.MovePathAttribute;
import field.object.life.FieldLife;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import java.awt.*;

public class NormalMoveFragment extends ActionMoveFragment {

    Point position;
    Point vposition;
    Point offset;
    short foothold;
    short fallStartFoothold;

    public NormalMoveFragment(byte movePathAttribute, PacketReader packetReader) {
        super(movePathAttribute, packetReader);
    }

    @Override
    public void apply(FieldLife life) {
        super.apply(life);

        life.setPosition(position);
        life.setFoothold(foothold);
    }

    @Override
    public void decodeData(PacketReader packet) {
        position = packet.readPoint();
        vposition = packet.readPoint();
        foothold = packet.readShort();
        if (movePathAttribute == MovePathAttribute.FALL_DOWN) {
            fallStartFoothold = packet.readShort();
        }
        offset = packet.readPoint();

        super.decodeData(packet);
    }

    @Override
    public void encodeData(PacketWriter packet) {
        packet.writePosition(position);
        packet.writePosition(vposition);
        packet.writeShort(foothold);
        if (movePathAttribute == MovePathAttribute.FALL_DOWN) {
            packet.writeShort(fallStartFoothold);
        }
        packet.writePosition(offset);

        super.encodeData(packet);
    }
}
