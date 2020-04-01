package field.movement.fragment;

import field.object.life.FieldLife;
import field.movement.MovePathAttribute;
import lombok.Getter;
import lombok.Setter;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import java.awt.*;

public class NormalMoveFragment extends ActionMoveFragment {

    @Getter @Setter Point position;
    @Getter @Setter Point vposition;
    @Getter @Setter Point offset;
    @Getter @Setter short foothold;
    @Getter @Setter short fallStartFoothold;

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
