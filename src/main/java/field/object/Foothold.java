package field.object;

import lombok.Getter;
import util.packet.PacketReader;

@Getter
public class Foothold {

    private int id, next, prev, x1, x2, y1, y2;

    public void generate(PacketReader r) {
        id = r.readInteger();
        x1 = r.readShort();
        x2 = r.readShort();
        y1 = r.readShort();
        y2 = r.readShort();
        next = r.readInteger();
        prev = r.readInteger();
    }
}
