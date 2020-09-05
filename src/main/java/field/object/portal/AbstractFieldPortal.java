package field.object.portal;

import util.packet.PacketReader;

import java.awt.*;

public abstract class AbstractFieldPortal {

    protected int id, targetMap;
    protected int type;
    protected String name, script = "", targetName;
    protected Point position;

    public int getId() {
        return id;
    }

    public int getTargetMap() {
        return targetMap;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getScript() {
        return script;
    }

    public String getTargetName() {
        return targetName;
    }

    public Point getPosition() {
        return position;
    }

    public void generate(PacketReader r) {
        name = r.readMapleString();
        targetName = r.readMapleString();
        boolean hasScript = r.readBool();
        if (hasScript) {
            script = r.readMapleString();
        }
        position = r.readPoint();
        id = r.readInteger();
        targetMap = r.readInteger();
        type = r.readInteger();
    }

    @Override
    public String toString() {
        return "AbstractFieldPortal{" +
                "id=" + id +
                ", targetMap=" + targetMap +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", script='" + script + '\'' +
                ", targetName='" + targetName + '\'' +
                ", position=" + position +
                '}';
    }
}
