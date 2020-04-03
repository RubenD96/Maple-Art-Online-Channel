package managers.flag;

import util.packet.IntegerValue;

public enum FieldFlag implements IntegerValue {

    ID(0x01),
    RETURN_MAP(0x02),
    MAP_AREA(0x04),
    FOOTHOLDS(0x08),
    FORCED_RETURN(0x10),
    FIELD_LIMIT(0x20),
    NAME(0x40),
    ON_ENTER(0x80),
    PORTALS(0x100),
    AREAS(0x200),
    LIFE(0x400);

    private final int flag;

    FieldFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public int getValue() {
        return flag;
    }

    @Override
    public void setValue(int value) {
        //...
    }
}
