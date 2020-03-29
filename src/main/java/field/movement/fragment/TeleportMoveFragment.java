package field.movement.fragment;

import field.life.FieldLife;
import lombok.Getter;
import lombok.Setter;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import java.awt.*;

public class TeleportMoveFragment extends ActionMoveFragment {

    @Getter @Setter Point position;
    @Getter @Setter short foothold;

    public TeleportMoveFragment(byte movePathAttribute, PacketReader packetReader) {
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
        foothold = packet.readShort();

        super.decodeData(packet);
    }

    @Override
    public void encodeData(PacketWriter packet) {
        packet.writePosition(position);
        packet.writeShort(foothold);

        super.encodeData(packet);
    }
}
