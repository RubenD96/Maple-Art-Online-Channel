package field.life;

import util.packet.Packet;

import java.awt.*;

public interface FieldObject {

    int getId();

    void setId(int id);

    FieldObjectType getFieldObjectType();

    Point getPosition();

    void setPosition(Point position);

    Packet getEnterFieldPacket();

    Packet getLeaveFieldPacket();
}
