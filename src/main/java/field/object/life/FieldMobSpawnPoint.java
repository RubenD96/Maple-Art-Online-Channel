package field.object.life;

import java.awt.*;

public class FieldMobSpawnPoint {

    private final int id;
    private final Point point;
    private final int rx0, rx1, cy, time;
    private final short fh;

    public FieldMobSpawnPoint(int id, Point point, int rx0, int rx1, int cy, int time, short fh) {
        this.id = id;
        this.point = point;
        this.rx0 = rx0;
        this.rx1 = rx1;
        this.cy = cy;
        this.time = time;
        this.fh = fh;
    }

    public int getId() {
        return id;
    }

    public Point getPoint() {
        return point;
    }

    public int getRx0() {
        return rx0;
    }

    public int getRx1() {
        return rx1;
    }

    public int getCy() {
        return cy;
    }

    public int getTime() {
        return time;
    }

    public short getFh() {
        return fh;
    }
}
