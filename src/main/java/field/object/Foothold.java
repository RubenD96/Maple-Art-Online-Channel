package field.object;

import util.packet.PacketReader;

public class Foothold {

    private int id, next, prev, x1, x2, y1, y2;

    public void decode(PacketReader r) {
        id = r.readInteger();
        x1 = r.readShort();
        x2 = r.readShort();
        y1 = r.readShort();
        y2 = r.readShort();
        next = r.readInteger();
        prev = r.readInteger();
    }

    public int getId() {
        return id;
    }

    public int getNext() {
        return next;
    }

    public int getPrev() {
        return prev;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }
}
