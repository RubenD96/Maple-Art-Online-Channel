package field.obj

import field.Field
import util.packet.Packet
import java.awt.Point

interface FieldObject {
    var id: Int
    var field: Field
    val fieldObjectType: FieldObjectType
    var position: Point
    val enterFieldPacket: Packet
    val leaveFieldPacket: Packet
}

/*
package field.object;

import field.Field;
import util.packet.Packet;

import java.awt.*;

public interface FieldObject {

    int getId();

    void setId(int id);

    Field getField();

    void setField(Field field);

    FieldObjectType getFieldObjectType();

    Point getPosition();

    void setPosition(Point position);

    Packet getEnterFieldPacket();

    Packet getLeaveFieldPacket();
}

 */