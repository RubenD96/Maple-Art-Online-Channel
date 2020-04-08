package field.object.life;

import client.Character;
import lombok.Getter;
import lombok.Setter;
import util.packet.Packet;

public abstract class AbstractFieldControlledLife extends AbstractFieldLife implements FieldControlledObject {

    private Character controller;
    @Setter protected int rx0, rx1, cy;
    @Setter @Getter protected String name;
    @Setter protected boolean hide;
    @Setter protected boolean f;

    @Override
    public Character getController() {
        return controller;
    }

    @Override
    public void setController(Character controller) {
        if (this.controller == null || !this.controller.equals(controller)) {
            if (controller != null) {
                controller.getControlledObjects().remove(this);

                if (controller.getField() == getField()) {
                    controller.write(getChangeControllerPacket(false));
                }
            }

            this.controller = controller;
            if (this.controller != null) {
                this.controller.getControlledObjects().add(this);
                this.controller.write(getChangeControllerPacket(true));
            }
        }
    }

    protected abstract Packet getChangeControllerPacket(boolean setAsController);
}
