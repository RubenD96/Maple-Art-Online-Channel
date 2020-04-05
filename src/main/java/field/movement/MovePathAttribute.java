package field.movement;

public final class MovePathAttribute {
    public final static byte NORMAL = 0x0;
    public final static byte JUMP = 0x1;
    public final static byte IMPACT = 0x2;
    public final static byte IMMEDIATE = 0x3;
    public final static byte TELEPORT = 0x4;
    public final static byte HANG_ON_BACK = 0x5;
    public final static byte ASSAULTER = 0x6;
    public final static byte ASSASSINATION = 0x7;
    public final static byte RUSH = 0x8;
    public final static byte STAT_CHANGE = 0x9;
    public final static byte SIT_DOWN = 0xA;
    public final static byte START_FALL_DOWN = 0xB;
    public final static byte FALL_DOWN = 0xC;
    public final static byte START_WINGS = 0xD;
    public final static byte WINGS = 0xE;
    public final static byte ARAN_ADJUST = 0xF;
    public final static byte MOB_TOSS = 0x10;
    public final static byte FLYING_BLOCK = 0x11;
    public final static byte DASH_SLIDE = 0x12;
    public final static byte BMAGE_ADJUST = 0x13;
    public final static byte FLASH_JUMP = 0x14;
    public final static byte ROCKET_BOOSTER = 0x15;
    public final static byte BACK_STEP_SHOT = 0x16;
    public final static byte MOB_POWER_KNOCK_BACK = 0x17;
    public final static byte VERTICAL_JUMP = 0x18;
    public final static byte CUSTOM_IMPACT = 0x19;
    public final static byte COMBAT_STEP = 0x1A;
    public final static byte HIT = 0x1B;
    public final static byte TIME_BOMB_ATTACK = 0x1C;
    public final static byte SNOWBALL_TOUCH = 0x1D;
    public final static byte BUFF_ZONE_EFFECT = 0x1E;
    public final static byte MOB_LADDER = 0x1F;
    public final static byte MOB_RIGHT_ANGLE = 0x20;
    public final static byte MOB_STOP_NODE_START = 0x21;
    public final static byte MOB_BEFORE_NODE = 0x22;
    public final static byte MOB_ATTACK_RUSH = 0x23;
    public final static byte MOB_ATTACK_RUSH_STOP = 0x24;

    private MovePathAttribute() {
    }
}
