package client.player;

public enum StatType {

    SKIN(0x01),
    FACE(0x02),
    HAIR(0x04),
    PET(0x08),
    LEVEL(0x10),
    JOB(0x20),
    STR(0x40),
    DEX(0x80),
    INT(0x100),
    LUK(0x200),
    HP(0x400),
    MAX_HP(0x800),
    MP(0x1000),
    MAX_MP(0x2000),
    AP(0x4000),
    SP(0x8000),
    EXP(0x10000),
    FAME(0x20000),
    MESO(0x40000),
    PET2(0x80000),
    PET3(0x100000),
    TEMP_EXP(0x200000);

    private final int stat;

    StatType(int stat) {
        this.stat = stat;
    }

    public int getStat() {
        return stat;
    }
}
