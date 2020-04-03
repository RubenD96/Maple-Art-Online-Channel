package field.object.life;

import client.Character;
import util.packet.Packet;

public abstract class AbstractFieldControlledLife extends AbstractFieldLife implements FieldControlledObject {

    private Character controller;

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
