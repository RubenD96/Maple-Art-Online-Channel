package net.maple;

public enum RecvOpcode {

    MIGRATE_IN(0x14),
    PONG(0x19),
    USER_MOVE(0x2C),
    QUICKSLOT_KEY_MAPPED_MODIFIED(0xD8),
    UPDATE_SCREEN_SETTING(0xDA);
    private int code;

    RecvOpcode(int code) {
        this.code = code;
    }

    public int getValue() {
        return code;
    }
}