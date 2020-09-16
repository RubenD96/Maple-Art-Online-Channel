package field.obj.life

import client.Character
import util.packet.Packet

abstract class AbstractFieldControlledLife : AbstractFieldLife(), FieldControlledObject {

    var rx0 = 0
    var rx1 = 0
    var cy = 0
    var hide = false
    var f = false
    lateinit var name: String

    override var controller: Character? = null
        set(controller) {
            if (this.controller == null || this.controller != controller) {
                if (controller != null) {
                    controller.controlledObjects.remove(this)
                    if (controller.field === this.field) {
                        controller.write(getChangeControllerPacket(false))
                    }
                }
                field = controller
                if (this.controller != null) {
                    this.controller!!.controlledObjects.add(this)
                    this.controller!!.write(getChangeControllerPacket(true))
                }
            }
        }

    protected abstract fun getChangeControllerPacket(setAsController: Boolean): Packet
}

/*
package field.object.life;

import client.Character;
import util.packet.Packet;

public abstract class AbstractFieldControlledLife extends AbstractFieldLife implements FieldControlledObject {

    private Character controller;
    protected int rx0, rx1, cy;
    protected String name;
    protected boolean hide;
    protected boolean f;

    public String getName() {
        return name;
    }

    public void setRx0(int rx0) {
        this.rx0 = rx0;
    }

    public void setRx1(int rx1) {
        this.rx1 = rx1;
    }

    public void setCy(int cy) {
        this.cy = cy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public void setF(boolean f) {
        this.f = f;
    }

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
 */