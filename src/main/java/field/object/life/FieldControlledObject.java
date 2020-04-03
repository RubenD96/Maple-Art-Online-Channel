package field.object.life;

import client.Character;
import field.object.FieldObject;

public interface FieldControlledObject extends FieldObject {

    Character getController();

    void setController(Character controller);
}
