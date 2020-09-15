package managers.flag

import util.packet.IntegerValue

enum class FieldFlag(override val value: Int) : IntegerValue {
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
    LIFE(0x400);}