package field.object.life;

import field.object.FieldObject;

public interface FieldLife extends FieldObject {

    byte getMoveAction();

    void setMoveAction(byte moveAction);

    short getFoothold();

    void setFoothold(short foothold);
}
