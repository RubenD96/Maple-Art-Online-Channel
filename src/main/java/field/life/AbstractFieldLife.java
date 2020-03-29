package field.life;

import field.movement.MovePath;
import util.packet.Packet;
import util.packet.PacketReader;

public abstract class AbstractFieldLife extends AbstractFieldObject implements FieldLife {

    private byte moveAction;
    private short foothold;

    @Override
    public byte getMoveAction() {
        return moveAction;
    }

    @Override
    public void setMoveAction(byte moveAction) {
        this.moveAction = moveAction;
    }

    @Override
    public short getFoothold() {
        return foothold;
    }

    @Override
    public void setFoothold(short foothold) {
        this.foothold = foothold;
    }

    public MovePath move(PacketReader packet) {
        MovePath path = new MovePath(packet);

        path.apply(this);
        return path;
    }
}
