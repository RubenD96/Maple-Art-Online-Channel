package field.object.portal;

import lombok.Getter;
import util.packet.PacketReader;

import java.awt.*;

@Getter
public abstract class AbstractFieldPortal {

    private int id, targetMap;
    private int type;
    private String name, script = "", targetName;
    private Point position;

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
