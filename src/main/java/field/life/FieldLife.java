package field.life;

public interface FieldLife extends FieldObject {

    byte getMoveAction();

    void setMoveAction(byte moveAction);

    short getFoothold();

    void setFoothold(short foothold);
}
