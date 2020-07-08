package field.object;

import field.Field;
import lombok.Getter;
import lombok.Setter;
import util.packet.Packet;

import java.awt.*;

public abstract class AbstractFieldObject implements FieldObject {

    protected Point position = new Point();
    protected @Getter @Setter Field field;
    protected int id;

    @Override
    public abstract FieldObjectType getFieldObjectType();

    @Override
    public abstract Packet getEnterFieldPacket();

    @Override
    public abstract Packet getLeaveFieldPacket();

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Point getPosition() {
        return new Point(position);
    }

    @Override
    public void setPosition(Point position) {
        this.position.x = position.x;
        this.position.y = position.y;
    }
}
