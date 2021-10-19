package managers.flag

import util.packet.IntegerValue

enum class FieldFlag(override val value: Int) : IntegerValue {
    ID(0x01),
    RETURN_MAP(0x02),
    MAP_AREA(0x04),
    FOOTHOLDS(0x08),
    FORCED_RETURN(0x10),
    FIELD_LIMIT(0x20),
    MAP_NAME(0x40),
    STREET_NAME(0x80),
    ON_ENTER(0x100),
    PORTALS(0x200),
    AREAS(0x400),
    LIFE(0x800),
    REACTOR(0x1000);
}