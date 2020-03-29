package field.life;

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
