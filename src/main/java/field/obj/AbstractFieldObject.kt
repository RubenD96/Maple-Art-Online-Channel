package field.obj

import field.Field
import util.packet.Packet
import java.awt.Point

abstract class AbstractFieldObject : FieldObject {

    override var position = Point()
    override lateinit var field: Field
    override var id = 0

    abstract override val enterFieldPacket: Packet
    abstract override val leaveFieldPacket: Packet
}
/*
package field.object;

import field.Field;
import util.packet.Packet;

import java.awt.*;

public abstract class AbstractFieldObject implements FieldObject {

    protected Point position = new Point();
    protected Field field;
    protected int id;

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public void setField(Field field) {
        this.field = field;
    }

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

 */